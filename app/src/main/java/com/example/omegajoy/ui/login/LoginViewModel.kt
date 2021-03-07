package com.example.omegajoy.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omegajoy.R
import com.example.omegajoy.data.LoginRepository
import com.example.omegajoy.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun preLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository.preLogin()

            if (result is Result.Success) {
                _loginResult.postValue(
                    LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
                )
            } else {
                _loginResult.postValue(LoginResult(error = R.string.login_failed))
            }
        }
    }

    fun login(username: String, password: String, login_mode: Boolean) {
        // can be launched in a separate asynchronous job
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginRepository.login(username, password, login_mode)

            if (result is Result.Success) {
                _loginResult.postValue(
                    LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
                )
            } else {
                _loginResult.postValue(LoginResult(error = R.string.login_failed))
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}