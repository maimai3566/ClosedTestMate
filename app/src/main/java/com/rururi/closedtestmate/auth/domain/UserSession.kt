package com.rururi.closedtestmate.auth.domain

import android.net.Uri

data class UserSession (
    val uid: String,
    val name: String?,
    val photoUrl: Uri?,
    val email: String? = null,
)