package com.rururi.closedtestmate.ui.recruitlist

import android.net.Uri
import com.rururi.closedtestmate.recruit.domain.RecruitStatus

data class RecruitListUiState(
    val isLoading: Boolean = false,
    val recruitList: List<RecruitListItem> = emptyList(),
    val error: Exception? = null
)

data class RecruitListItem(
    val id: String,
    val appName: String,
    val appIcon: Uri?,
    val status: RecruitStatus,
    val postedAt: Long,
    val authorName: String,
    val authorIcon: Uri?
)