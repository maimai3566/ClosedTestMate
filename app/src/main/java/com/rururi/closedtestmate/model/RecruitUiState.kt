package com.rururi.closedtestmate.model

import android.R.attr.description
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.rururi.closedtestmate.R

/**
 * テスター募集投稿のUI表示用状態クラス
 */
data class RecruitUiState(
    val id: String = "",          // 投稿ID（FirestoreのドキュメントIDなど）
    val appName: String = "",     // アプリ名（表示タイトル）
    val appIcon: Uri? = null,     // アプリアイコン（URI型）
    val status: RecruitStatus = RecruitStatus.Open,      // ステータス（例：募集中／募集完了）
    val details: List<DetailContent> = emptyList(), // 詳細項目（テキスト／画像）
    val groupUrl: String = "",    // GoogleグループのURL（コミュニケーション用）
    val appUrl: String = "",      // Androidアプリ配布URL（Firebase App Distributionなど）
    val webUrl: String = "",      // Web経由の参加URL（非公開フォームなど）
    val postedAt: Long = 0L,       // 投稿日時（UnixTimeミリ秒）
    val isSaved: Boolean = false,    //Firestoreに保存したかどうか

    val authorId: String = "",     //投稿したユーザーID
    val authorName: String = "",   //投稿したユーザー名
    val authorIcon: Uri? = null,   //投稿したユーザーアイコン（URI型）
) {
    val isValid: Boolean
        get() = appName.isNotBlank() && details.isNotEmpty() && groupUrl.isNotBlank() && appUrl.isNotBlank()
}



