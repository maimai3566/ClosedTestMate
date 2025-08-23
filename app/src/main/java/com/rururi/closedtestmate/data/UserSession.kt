package com.rururi.closedtestmate.data

import android.net.Uri

data class UserSession (
    val uid: String,
    val name: String?,
    val photoUrl: Uri?,
    val email: String? = null,
)