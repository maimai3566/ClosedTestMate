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
    }
}