package com.rururi.closedtestmate.auth.login

import com.rururi.closedtestmate.auth.common.AuthError

data class LoginUiState(
    val email: String = "",
    val pw: String = "",
    //isLoadingは二重タップ防止、UI表示制御に使う。tureのときロード中
    val isLoading: Boolean = false,
    val error: AuthError? = null,
    val success: Boolean = false,
) {
    val isValid = email.isNotBlank() && pw.isNotBlank() && !isLoading   //登録ボタン用
}
