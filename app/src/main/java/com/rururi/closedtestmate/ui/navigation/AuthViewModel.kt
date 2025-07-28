package com.rururi.closedtestmate.ui.navigation

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.rururi.closedtestmate.model.UserProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor():ViewModel() {
    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState : StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    init {
        updateIsLoggedIn()
    }

    //ログイン状態を管理
    fun updateIsLoggedIn () {
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        _uiState.update { it.copy(isLoggedIn = isLoggedIn) }
    }
}