package com.rururi.closedtestmate.ui.recruitdetail

import android.net.Uri
import com.rururi.closedtestmate.recruit.domain.DetailContent
import com.rururi.closedtestmate.recruit.domain.RecruitStatus

data class RecruitDetailUiState(
    val isLoading: Boolean = false,
    val detail: RecruitDetailItem? = null,
    val error: Exception? = null
)

data class RecruitDetailItem(
    val id: String = "",
    val appName: String = "",
    val appIcon: Uri? = null,
    val status: RecruitStatus = RecruitStatus.Open,
    val details: List<DetailContent> = emptyList(),
    val groupUrl: String = "",
    val appUrl: String = "",
    val webUrl: String = "",
    val postedAt: Long = 0,
    val authorId: String = "",
    val authorName: String = "",
    val authorIcon: Uri? = null,
)


