package com.example.omegabot.ui.robotmanager.add_robot

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.blikoon.qrcodescanner.QrCodeActivity
import com.example.omegabot.MainActivity
import com.example.omegabot.R
import com.example.omegabot.ui.FullFrameFragment


class AddRobotFragment : FullFrameFragment() {
    private lateinit var editTextId: EditText
    val robotViewModel: AddRobotViewModel by viewModels {
        AddRobotModelFactory((activity as MainActivity).database, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_robot, container, false)

        editTextId = root.findViewById(R.id.edit_text_id)

        val buttonToHome: ImageButton = root.findViewById(R.id.button_to_home)
        buttonToHome.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_nav_add_robot_to_nav_home)
        )
        val buttonToMenu: ImageButton = root.findViewById(R.id.button_to_menu)
        buttonToMenu.setOnClickListener {
            (activity as MainActivity).openDrawer()
        }

        val buttonAdd: Button = root.findViewById(R.id.button_add)
        buttonAdd.setOnClickListener {
            robotViewModel.addRobot(editTextId.text.toString())
        }

        val buttonBack: Button = root.findViewById(R.id.button_back)
        buttonBack.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_nav_add_robot_to_nav_robot_manager)
        )

        val qrScanner: ImageButton = root.findViewById(R.id.qr_scanner)
        qrScanner.setOnClickListener {
            // проверка наличия разрешения на использование камеры
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    MY_PERMISSIONS_REQUEST_CAMERA
                )
            } else {
                val i = Intent(activity, QrCodeActivity::class.java)
                startActivityForResult(i, REQUEST_CODE_QR_SCAN)
            }
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null) return
            //Getting the passed result
            val result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult")
            editTextId.setText(result)
        }
    }

    fun backToManager() {
        Navigation.createNavigateOnClickListener(R.id.action_nav_add_robot_to_nav_robot_manager)
    }

    companion object {
        private val REQUEST_CODE_QR_SCAN = 101
        private val MY_PERMISSIONS_REQUEST_CAMERA = 100
    }
}