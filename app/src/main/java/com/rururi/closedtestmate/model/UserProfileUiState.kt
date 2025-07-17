package com.rururi.closedtestmate.model

data class UserProfileUiState(
    val userId: String = "",
    val name: String = "ゲストさん",
    val email: String = "",
    val pw: String = "",
    val photoUrl: String? = null,   //プロフィール画像のURL
    val error: String = "",
    val hasRegistered: Boolean = false,   //登録済みかどうか
    val isLoggedIn: Boolean = false,   //ログイン中かどうか
    val isAnonymous: Boolean = false,   //匿名かどうか
    val isLoading: Boolean = false,   //ローディング中かどうか
)
