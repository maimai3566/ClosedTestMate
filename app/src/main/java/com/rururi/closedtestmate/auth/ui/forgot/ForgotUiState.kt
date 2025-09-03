package com.rururi.closedtestmate.auth.ui.forgot

import android.util.Patterns
import com.rururi.closedtestmate.auth.common.AuthError

data class ForgotUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: AuthError? = null,

    val resetMailSent: Boolean = false,
    val msg: String = ""
) {
    val isValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
