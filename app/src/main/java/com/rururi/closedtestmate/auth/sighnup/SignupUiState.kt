package com.rururi.closedtestmate.auth.sighnup

import com.rururi.closedtestmate.auth.common.AuthError

data class SignupUiState(
    val email: String = "",
    val pw: String = "",
    val pw2: String = "",

    val isLoading: Boolean = false,
    val error: AuthError? = null,
    val success: Boolean = false
) {
    val isValid = email.isNotBlank() && pw.isNotBlank() && pw2.isNotBlank() && pw == pw2
}
