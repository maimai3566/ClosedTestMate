package com.rururi.closedtestmate.ui.login

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.play.integrity.internal.u
import com.rururi.closedtestmate.model.UserProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.rururi.closedtestmate.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.update

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    //汎用の更新関数
    fun updateUiState(update: UserProfileUiState.() -> UserProfileUiState) {
        _uiState.update (update)
    }

    //状態をリセット
    fun resetUiState() {
        _uiState.update {
            it.copy(
                isLoggedIn = false,
                error = "",
                success = "",
                email = "",
                pw = "",
                userId = "",
                name = "",
                photoUrl = "",
                isAnonymous = false,
                isLoading = false,
            )
        }
    }

    //パスワード忘れ
    fun forgotPassword(onSuccess:()->Unit) {
        val email = _uiState.value.email
        val success = context.getString(R.string.forgot_pw_success)

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                _uiState.update {
                    it.copy(
                        success = success,
                        error = "",
                    )
                }
//                onSuccess() //ログイン画面に
            }
            .addOnFailureListener { exception ->
                val code = (exception as? FirebaseAuthException)?.errorCode
                val authError = AuthError.fromException(code ?: "")
                val errorMsg = context.getString(authError.resId)
                Log.d("ruruV", "era-: $code")
                _uiState.update {
                    it.copy(
                        error = errorMsg,
                        success = "",
                        isLoading = false,
                    )
                }
            }
    }

    //Firebaseにユーザ情報を新規登録
    fun signUp(onSuccess:()->Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.pw

        _uiState.update { it.copy(isLoading = true) }   //ロード中
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = auth.currentUser
                _uiState.update {
                    it.copy(
                        userId = user?.uid ?: "",
                        email = user?.email ?: "",
                        isLoggedIn = true,
                        isLoading = false,
                        success = context.getString(R.string.signup_success),
                        error = ""
                    )
                }
                onSuccess()
            }
            .addOnFailureListener { exception ->
                val code = (exception as? FirebaseAuthException)?.errorCode
                val authError = AuthError.fromException(code ?: "")
                val errorMsg = context.getString(authError.resId)
                Log.d("ruruV", "era-: $code")
                _uiState.update {
                    it.copy(
                        error = errorMsg,
                        isLoading = false,
                    )
                }
            }
    }

    //ログイン
    fun login(onSuccess:()->Unit) {
        val email = _uiState.value.email
        val password = _uiState.value.pw

        auth.signInWithEmailAndPassword(email, password)    //メールとPWでログイン
            .addOnSuccessListener {
                val user = auth.currentUser
                _uiState.update {
                    it.copy(
                        userId = user?.uid ?: "",
                        name = user?.displayName ?: "",
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
                val code =  when {
                    exception is FirebaseAuthInvalidUserException -> exception.errorCode
                    exception is FirebaseAuthInvalidCredentialsException -> exception.errorCode
                    exception is FirebaseAuthUserCollisionException -> exception.errorCode
                    exception is FirebaseAuthException -> exception.errorCode
                    else -> null
                }
                val authError = AuthError.fromException(code ?: "")
                val errorMsg = context.getString(authError.resId)
                Log.d("ruruV", "era-: $code")
                _uiState.update {
                    it.copy(
                        error = errorMsg,
                        isLoading = false,
                    )
                }
            }
    }
}

