package com.rururi.closedtestmate.model

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
    val description: String = "", // 募集の概要・目的・内容など
    val groupUrl: String = "",    // GoogleグループのURL（コミュニケーション用）
    val appUrl: String = "",      // Androidアプリ配布URL（Firebase App Distributionなど）
    val webUrl: String = "",      // Web経由の参加URL（非公開フォームなど）
    val postedAt: Long = 0L,       // 投稿日時（UnixTimeミリ秒）
    val isSaved: Boolean = false    //Firestoreに保存したかどうか
)

sealed class RecruitStatus(
    @StringRes val labelResId: Int,
    @DrawableRes val iconResId: Int,
    val fireStoreLabel: String = ""
) {
    object Open: RecruitStatus(R.string.status_open, R.drawable.open,"募集中")
    object Closed: RecruitStatus(R.string.status_closed, R.drawable.closed,"募集終了")
    object Released: RecruitStatus(R.string.status_released, R.drawable.released,"リリース済")

    companion object {
        // Firestoreのステータス文字列からステータスオブジェクトに変換する
        fun fromFirestoreString(status: String): RecruitStatus = when (status) {
            Open.fireStoreLabel -> Open
            Closed.fireStoreLabel -> Closed
            Released.fireStoreLabel -> Released
            else -> Open
        }
    }
}

//ステータスを文字列に変換する関数
fun RecruitStatus.toFirestoreString(): String = when (this) {
    RecruitStatus.Open -> "募集中"
    RecruitStatus.Closed -> "募集終了"
    RecruitStatus.Released -> "リリース済"
}