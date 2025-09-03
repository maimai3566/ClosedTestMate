package com.rururi.closedtestmate.app.navigation

import android.net.Uri


sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object RecruitList : Screen("recruit_list")
    data object RecruitNew : Screen("recruit_new?status={status}&appName={appName}") {
        fun route(
            status: String? = null,
            appName: String? = null,
        ): String {
            val q = buildList {
                status?.let{ add("status=${Uri.encode(it)}") }
                appName?.let{ add("appName=${Uri.encode(it)}") }
            }.joinToString("&")
            return if (q.isEmpty()) "recruit_new" else "recruit_new?$q"
        }
    }
    object RecruitDetail : Screen("recruit_detail/{recruitId}") {
        fun createRoute(recruitId: String) = "recruit_detail/$recruitId"
    }
    object Search : Screen("search")
    object Favorite : Screen("favorite")
    object Profile : Screen("profile")

}