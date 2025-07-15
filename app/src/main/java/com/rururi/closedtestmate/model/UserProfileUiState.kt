package com.rururi.closedtestmate.model

data class UserProfileUiState(
    val userId: String = "",
    val displayName: String = "ゲストさん",
    val email: String = "",
    val pw: String = "",
    val error: String = "",
    val hasRegistered: Boolean = false,   //登録済みかどうか
    val isLoggedIn: Boolean = false,   //ログイン中かどうか
    val photoUrl: String? = null,   //プロフィール画像のURL
    val isAnonymous: Boolean = false,   //匿名かどうか
    val isLoading: Boolean = false,   //ローディング中かどうか
)
