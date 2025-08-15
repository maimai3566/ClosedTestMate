package com.rururi.closedtestmate.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rururi.closedtestmate.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    val isLoggedIn: StateFlow<Boolean> = authRepository.currentUserFlow
        .map { it != null }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000),
            initialValue = authRepository.currentUser() != null
        )

    fun logout() {
        authRepository.signOut()
    }
}