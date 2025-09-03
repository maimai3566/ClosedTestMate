package com.rururi.closedtestmate.auth.ui.forgot

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.rururi.closedtestmate.core.util.toAuthError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ForgotViewModel @Inject constructor(): ViewModel() {
    private val _uiState = MutableStateFlow(ForgotUiState())
    val uiState: StateFlow<ForgotUiState> = _uiState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    //汎用更新
    fun updateUiState(uiStateItem: ForgotUiState.() -> ForgotUiState) {
       _uiState.update(uiStateItem)
    }

    //パスワード忘れ
    fun forgotPassword(email: String) {
        _uiState.update { it.copy(isLoading = true) }   //ロード中
        auth.sendPasswordResetEmail(email)   //pw再設定メール送信
            .addOnSuccessListener { //成功時
                _uiState.update {
                    it.copy(isLoading = false, success = true, error = null)
                }
            }
            .addOnFailureListener { exception -> //失敗時
                val authError = exception.toAuthError()
                _uiState.update {
                    it.copy(isLoading = false, error = authError, success = false)
                }
            }
    }

}