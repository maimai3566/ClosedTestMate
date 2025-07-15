package com.rururi.closedtestmate.ui.login

import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.rururi.closedtestmate.model.UserProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.update

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(onSuccess:()->Unit,onError:(String)->Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.pw

        Log.d("ruruV", "ログイン処理を開始します。")
        auth.signInWithEmailAndPassword(email, password)    //メールとPWでログイン
            .addOnSuccessListener {
                val user = auth.currentUser
                _uiState.update {
                    it.copy(
                        userId = user?.uid ?: "",
                        displayName = user?.displayName ?: "",
                        email = user?.email ?: "",
                        photoUrl = user?.photoUrl?.toString() ?: "",
                        isAnonymous = user?.isAnonymous ?: false,
                        isLoggedIn = true,
                        isLoading = false,
                        error = ""
                    )
                }
                onSuccess()
            }
            .addOnFailureListener { exception ->
                val errorMsg = when(exception) {
                    is FirebaseAuthInvalidUserException -> "このメールアドレスは登録されていません"
                    is FirebaseAuthInvalidCredentialsException -> "メールアドレスまたはパスワードが間違っています"
                    else -> "ログインに失敗しました"
                }
                _uiState.update {
                    it.copy(
                        error = errorMsg,
                        isLoading = false,
                    )
                }
                onError(_uiState.value.error)
            }
    }

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email
            )
        }
    }

    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                pw = password
            )
        }
    }
}