package com.rururi.closedtestmate.auth.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.rururi.closedtestmate.ui.components.toAuthError
import dagger.hilt.android.lifecycle.HiltViewModel
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
}