package com.rururi.closedtestmate.auth.common

import androidx.annotation.StringRes
import com.rururi.closedtestmate.R

sealed class AuthError(@StringRes val resId: Int) {
    object InvalidEmail : AuthError(R.string.invalid_email_address)
    object EmailAlreadyInUse : AuthError(R.string.email_already_in_use)
    object WeakPassword : AuthError(R.string.password_should_be_at_least_6_characters)
    object AccountExistsWithDifferentCredential : AuthError(R.string.account_exists_with_different_credential)
    object UserNotFound : AuthError(R.string.user_not_found)
    object NetworkError : AuthError(R.string.network_error)
    object UnknownError : AuthError(R.string.unknown_error)
    object InvalidCredentials : AuthError(R.string.invalid_credentials)

    //プロフィール
    object NameEmpty : AuthError(R.string.name_cannot_be_empty)
    data class NameTooLong(val maxLen: Int) : AuthError(R.string.name_too_long)
    object RequiresRecentLogin : AuthError(R.string.requires_recent_login)

    companion object {
        fun fromException(errorCode: String?): AuthError = when(errorCode) {
            "ERROR_INVALID_EMAIL" -> InvalidEmail
            "ERROR_EMAIL_ALREADY_IN_USE" -> EmailAlreadyInUse
            "ERROR_WEAK_PASSWORD" -> WeakPassword
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> AccountExistsWithDifferentCredential
            "ERROR_USER_NOT_FOUND" -> UserNotFound
            "ERROR_NETWORK_REQUEST_FAILED" -> NetworkError
            "ERROR_INVALID_CREDENTIAL" -> InvalidCredentials
            else -> UnknownError
        }

        //プロフィールとかの汎用
        fun fromThrowable(t: Throwable?): AuthError {
            val msg = (t?.message ?: "").lowercase()
            return when {
                ("recent" in msg && "login" in msg) -> RequiresRecentLogin
                "timeout" in msg || "network" in msg || "unable to resolve host" in msg -> NetworkError
                "invalid credential" in msg -> InvalidCredentials
                else -> UnknownError
            }
        }
    }
}