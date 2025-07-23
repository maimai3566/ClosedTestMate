package com.rururi.closedtestmate.model

data class UserProfileUiState(
    val userId: String = "",
    val name: String = "ゲストさん",
    val email: String = "",
    val pw: String = "",
    val pw2: String = "",       //confirm password用
    val photoUrl: String? = null,   //プロフィール画像のURL
    val error: String = "",     //エラーメッセージ
    val success: String = "",   //成功メッセージ
    val hasRegistered: Boolean = false,   //登録済みかどうか
    val isLoggedIn: Boolean = false,   //ログイン中かどうか
    val isAnonymous: Boolean = false,   //匿名かどうか
    val isLoading: Boolean = false,   //ローディング中かどうか
)
