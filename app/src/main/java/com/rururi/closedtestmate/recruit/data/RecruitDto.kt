package com.rururi.closedtestmate.recruit.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

//Firestoreと直接やり取りするクラス
data class RecruitDto(
    val appName: String = "",
    val appIcon: String? = null,
    val status: String = "",
    val details: List<DetailDto> = emptyList(),
    val groupUrl: String = "",
    val appUrl: String = "",
    val webUrl: String = "",
    @ServerTimestamp val postedAt: Timestamp? = null,
    val authorId: String = "",
    val authorName: String = "",
    val authorIcon: String? = null
)


