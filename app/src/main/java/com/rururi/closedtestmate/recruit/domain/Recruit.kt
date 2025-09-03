package com.rururi.closedtestmate.recruit.domain

import android.net.Uri
import com.rururi.closedtestmate.recruit.domain.RecruitStatus

//Firestoreに保存するクラス
data class Recruit(
    val id: String,
    val appName: String,
    val appIcon: Uri?,
    val status: RecruitStatus,
    val details: List<DetailContent>,
    val groupUrl: String,
    val appUrl: String,
    val webUrl: String,
    val postedAt: Long,
    val authorId: String,
    val authorName: String,
    val authorIcon: Uri?
)