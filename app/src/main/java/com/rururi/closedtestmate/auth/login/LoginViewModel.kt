package com.rururi.closedtestmate.auth.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.rururi.closedtestmate.auth.common.AuthError
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(): ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    //汎用更新
    fun updateUiState(update: LoginUiState.() -> LoginUiState) {
        _uiState.update(update)
    }

    //ログインボタン押下
    fun login(email: String, pw: String) {
        //初期化
        _uiState.update { it.copy(isLoading = true, error = null, success = false) }

        auth.signInWithEmailAndPassword(email, pw)
            .addOnSuccessListener {
                // ログイン成功
                _uiState.update {
                    it.copy(isLoading = false, error = null, success = true)
                }

            }
            .addOnFailureListener { exception ->
                // ログイン失敗
                val authError = exception.toAuthError()
                _uiState.update {
                    it.copy(isLoading = false, error = authError, success = false)
                }
            }
    }

    //例外→AuthErrorの共通変換
    private fun Throwable.toAuthError(): AuthError {
        val code = when (this) {
            is FirebaseAuthInvalidUserException -> this.errorCode
            is FirebaseAuthInvalidCredentialsException -> this.errorCode
            is FirebaseAuthUserCollisionException -> this.errorCode
            is FirebaseAuthException -> this.errorCode
            else -> null
        }
        return AuthError.fromException(code)
    }
}