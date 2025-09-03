package com.rururi.closedtestmate.recruit.data

import android.net.Uri
import androidx.core.net.toUri
import com.rururi.closedtestmate.core.util.toUriOrNull
import com.rururi.closedtestmate.recruit.domain.DetailContent
import com.rururi.closedtestmate.recruit.domain.Recruit
import com.rururi.closedtestmate.recruit.domain.RecruitStatus

//Firestoreに保存するクラスをDomain型に変換
fun RecruitDto.toDomain(id: String): Recruit =
    Recruit(
        id = id,
        appName = appName,
        appIcon = appIcon.toUriOrNull(),
        status = RecruitStatus.fromFirestoreString(status),
        details = details.map { it.toDomain() },
        groupUrl = groupUrl,
        appUrl = appUrl,
        webUrl = webUrl,
        postedAt = postedAt?.toDate()?.time ?: 0L,
        authorId = authorId,
        authorName = authorName,
        authorIcon = authorIcon.toUriOrNull()
    )

fun DetailDto.toDomain(): DetailContent = when (type) {
    "text" -> DetailContent.Text(id = id, text = text ?: "")
    "image" -> DetailContent.Image(id = id, uri = uri?.toUri() ?: Uri.EMPTY)
    else -> DetailContent.Text(id = id, text = text ?: "")  //フォールバック
}

//Domain型をFirestoreに保存するクラスに変換
fun Recruit.toDto(): RecruitDto =
    RecruitDto(
        appName = appName,
        appIcon = appIcon?.toString(),
        status = when (status) {
            is RecruitStatus.Open -> RecruitStatus.Open.fireStoreLabel
            is RecruitStatus.Closed -> RecruitStatus.Closed.fireStoreLabel
            is RecruitStatus.Released -> RecruitStatus.Released.fireStoreLabel
        },
        details = details.map { it.toDto() },
        groupUrl = groupUrl,
        appUrl = appUrl,
        webUrl = webUrl,
        postedAt = null,
        authorId = authorId,
        authorName = authorName,
        authorIcon = authorIcon?.toString()
    )

private fun DetailContent.toDto(): DetailDto = when (this) {
    is DetailContent.Text -> DetailDto(type = "text", id = id, text = text)
    is DetailContent.Image -> DetailDto(type = "image", id = id, uri = uri.toString())
}