package com.rururi.closedtestmate.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rururi.closedtestmate.data.AuthRepository
import com.rururi.closedtestmate.model.UserProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    authRepository: AuthRepository
):ViewModel() {
    val isLoggedIn: StateFlow<Boolean> = authRepository.currentUserFlow
        .map { it != null }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = authRepository.currentUser() != null
        )
//    private val _uiState = MutableStateFlow(UserProfileUiState())
//    val uiState : StateFlow<UserProfileUiState> = _uiState.asStateFlow()
//
//    init {
//        updateIsLoggedIn()
//    }
//
//    //ログイン状態を管理
//    fun updateIsLoggedIn () {
//        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
//        _uiState.update { it.copy(isLoggedIn = isLoggedIn) }
//    }

}

