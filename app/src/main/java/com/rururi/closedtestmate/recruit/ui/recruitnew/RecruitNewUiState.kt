package com.rururi.closedtestmate.ui.recruitnew

import android.net.Uri
import com.rururi.closedtestmate.recruit.domain.DetailContent
import com.rururi.closedtestmate.recruit.domain.RecruitStatus

data class RecruitNewUiState(
//    val isLoading: Boolean = false,
    val input: RecruitNewItem = RecruitNewItem(),   //入力部分だけ切り出し
    val isSaving: Boolean = false,  //保存中かどうか
//    val saveStatus: SaveStatus = SaveStatus.Idle,
//    val error: Exception? = null,
)

data class RecruitNewItem(
    val appName: String = "",
    val appIcon: Uri? = null,
    val status: RecruitStatus = RecruitStatus.Open,
    val details: List<DetailContent> = emptyList(),
    val groupUrl: String = "",
    val appUrl: String = "",
    val webUrl: String = "",
){
    val isValid: Boolean
        get() = appName.isNotBlank() && groupUrl.isNotBlank() && appUrl.isNotBlank() && details.isNotEmpty()
}

