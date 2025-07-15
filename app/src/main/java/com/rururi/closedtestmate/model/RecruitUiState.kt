package com.rururi.closedtestmate.model

/**
 * テスター募集投稿のUI表示用状態クラス
 */
data class RecruitUiState(
    val id: String = "",          // 投稿ID（FirestoreのドキュメントIDなど）
    val appName: String = "",     // アプリ名（表示タイトル）
    val appIcon: String = "",     // アプリアイコン（URLまたはBase64など）
    val status: String = "",      // ステータス（例：募集中／募集完了）
    val description: String = "", // 募集の概要・目的・内容など
    val groupUrl: String = "",    // GoogleグループのURL（コミュニケーション用）
    val appUrl: String = "",      // Androidアプリ配布URL（Firebase App Distributionなど）
    val webUrl: String = "",      // Web経由の参加URL（非公開フォームなど）
    val postedAt: Long = 0L       // 投稿日時（UnixTimeミリ秒）
)
