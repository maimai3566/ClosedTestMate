package com.rururi.closedtestmate.ui.profile

import com.rururi.closedtestmate.auth.common.AuthError
import com.rururi.closedtestmate.auth.domain.UserSession

data class ProfileUiState(
    val session: UserSession? = null,
    //編集ダイアログ用
    val showEditDialog: Boolean = false,
    val newName: String = "",
    val maxLen: Int = 24,

    val isSaving: Boolean = false,
    val success: Boolean = false,
    val error: AuthError? = null
) {
    val isValid:Boolean
        get() = !isSaving && newName.isNotBlank() && newName.length <= maxLen
}
