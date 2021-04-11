package com.example.omegabot.ui.home

import android.annotation.SuppressLint
import org.json.JSONObject
import java.net.Socket
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal class SignallingClientKotlin {


    internal interface SignalingInterface {
        fun onRemoteHangUp(msg: String)

        fun onOfferReceived(data: JSONObject)

        fun onAnswerReceived(data: JSONObject)

        fun onIceCandidateReceived(data: JSONObject)

        fun onTryToStart()

        fun onCreatedRoom()

        fun onJoinedRoom()

        fun onNewPeerJoined()
    }

    companion object {
        private var roomName: String? = null

        init {
            if (roomName == null) {
                roomName = "some_room_name"
            }
        }

        private var socket: Socket? = null
        var isChannelReady = false
        var isInitiator = false
        var isStarted = false
        private var callback: SignalingInterface? = null

        //This piece of code should not go into production?
        //This will help in cases where the node server is running in non-https server and you want to ignore the warnings
        private val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }
        })


        fun init(signalingInterface: SignalingInterface) {
            this.callback = signalingInterface
            try {
                val sslcontext = SSLContext.getInstance("TLS")
                sslcontext.init(null, trustAllCerts, null)
                IO.setDefaultHostnameVerifier { _, _ -> true }
                IO.setDefaultSSLContext(sslcontext)
                //set the socket.io url here
                socket = IO.socket("your_socket_io_instance_url_with_port")
                socket?.connect()
                Log.d("SignallingClient", "init() called")

                roomName?.let { emitInitStatement(it) }


                //room created event.
                socket?.on("created") { args ->
                    Log.d(
                        "SignallingClient",
                        "created call() called with: args = [" + Arrays.toString(args) + "]"
                    )
                    isInitiator = true
                    callback?.onCreatedRoom()
                }

                //room is full event
                socket?.on("full") { args ->
                    Log.d(
                        "SignallingClient",
                        "full call() called with: args = [" + Arrays.toString(args) + "]"
                    )
                }

                //peer joined event
                socket?.on("join") { args ->
                    Log.d(
                        "SignallingClient",
                        "join call() called with: args = [" + Arrays.toString(args) + "]"
                    )
                    isChannelReady = true
                    callback?.onNewPeerJoined()
                }

                //when you joined a chat room successfully
                socket?.on("joined") { args ->
                    Log.d(
                        "SignallingClient",
                        "joined call() called with: args = [" + Arrays.toString(args) + "]"
                    )
                    isChannelReady = true
                    callback?.onJoinedRoom()
                }

                //log event
                socket?.on("log") { args ->
                    Log.d(
                        "SignallingClient",
                        "log call() called with: args = [" + Arrays.toString(args) + "]"
                    )
                }

                //bye event
                socket?.on("bye") { args -> callback?.onRemoteHangUp(args[0] as String) }

                //messages - SDP and ICE candidates are transferred through this
                socket?.on("message") { args ->
                    Log.d(
                        "SignallingClient",
                        "message call() called with: args = [" + Arrays.toString(args) + "]"
                    )
                    when (args[0]) {
                        is String -> {
                            Log.d("SignallingClient", "String received :: " + args[0])
                            val data = args[0] as String
                            if (data.equals("got user media", ignoreCase = true)) {
                                callback?.onTryToStart()
                            }
                            if (data.equals("bye", ignoreCase = true)) {
                                callback?.onRemoteHangUp(data)
                            }
                        }
                        is JSONObject -> try {
                            val data = args[0] as JSONObject
                            Log.d("SignallingClient", "Json Received :: " + data.toString())
                            val type = data.getString("type")
                            if (type.equals("offer", ignoreCase = true)) {
                                callback?.onOfferReceived(data)
                            } else if (type.equals("answer", ignoreCase = true) && isStarted) {
                                callback?.onAnswerReceived(data)
                            } else if (type.equals("candidate", ignoreCase = true) && isStarted) {
                                callback?.onIceCandidateReceived(data)
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }

        }

        private fun emitInitStatement(message: String) {
            Log.d(
                "SignallingClient",
                "emitInitStatement() called with: event = [create or join], message = [$message]"
            )
            socket?.emit("create or join", message)
        }

        fun emitMessage(message: String) {
            Log.d("SignallingClient", "emitMessage() called with: message = [$message]")
            socket?.emit("message", message)
        }

        fun emitMessage(message: SessionDescription) {
            try {
                Log.d("SignallingClient", "emitMessage() called with: message = [$message]")
                val obj = JSONObject()
                obj.put("type", message.type.canonicalForm())
                obj.put("sdp", message.description)
                Log.d("emitMessage", obj.toString())
                socket?.emit("message", obj)
                Log.d("vivek1794", obj.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }


        fun emitIceCandidate(iceCandidate: IceCandidate) {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("type", "candidate")
                jsonObject.put("label", iceCandidate.sdpMLineIndex)
                jsonObject.put("id", iceCandidate.sdpMid)
                jsonObject.put("candidate", iceCandidate.sdp)
                socket?.emit("message", jsonObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun close() {
            socket?.emit("bye", roomName)
            socket?.disconnect()
            socket?.close()
        }
    }
}