package com.rururi.closedtestmate.recruit.ui

import android.net.Uri
import com.rururi.closedtestmate.recruit.domain.Recruit
import com.rururi.closedtestmate.ui.recruitdetail.RecruitDetailItem
import com.rururi.closedtestmate.ui.recruitlist.RecruitListItem
import com.rururi.closedtestmate.ui.recruitnew.RecruitNewItem

//domain－＞uiの変換を担当
fun Recruit.toListItem(): RecruitListItem = RecruitListItem(
    id = id,
    appName = appName,
    appIcon = appIcon,
    status = status,
    postedAt = postedAt,
    authorName = authorName,
    authorIcon = authorIcon
)

fun Recruit.toDetailItem(): RecruitDetailItem = RecruitDetailItem(
    id = id,
    appName = appName,
    appIcon = appIcon,
    status = status,
    details = details,
    groupUrl = groupUrl,
    appUrl = appUrl,
    webUrl = webUrl,
    postedAt = postedAt,
    authorId = authorId,
    authorName = authorName,
    authorIcon = authorIcon
)

//ui－＞domain
fun RecruitNewItem.toDomain(
    authorId: String,
    authorName: String,
    authorIcon: Uri?
): Recruit = Recruit(
    id = "",        //Firestore側で採番
    appName = appName,
    appIcon = appIcon,
    status = status,
    details = details,
    groupUrl = groupUrl,
    appUrl = appUrl,
    webUrl = webUrl,
    postedAt = 0L,  //Firestore登録時に自動的にサーバのタイムスタンプが入る
    authorId = authorId,
    authorName = authorName,
    authorIcon = authorIcon
)