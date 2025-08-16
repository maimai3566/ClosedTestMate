package com.rururi.closedtestmate.auth.sighnup

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.auth.common.AuthError
import com.rururi.closedtestmate.ui.components.toAuthError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor() : ViewModel(){
    val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState : StateFlow<SignupUiState> = _uiState.asStateFlow()

    //汎用的にUI状態を更新する
    fun updateUiState(update: SignupUiState.() -> SignupUiState) {
        _uiState.update(update)
    }

    //signupしてFirebaseに登録する
    fun signup(email: String, pw: String) {
        //初期化
        _uiState.update { it.copy(isLoading = true, error = null, success = false) }

        auth.createUserWithEmailAndPassword(email, pw)
            .addOnSuccessListener {
                _uiState.update { it.copy(isLoading = false, error = null, success = true) }
            }
            .addOnFailureListener { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.toAuthError(), success = false) }
            }
    }
}