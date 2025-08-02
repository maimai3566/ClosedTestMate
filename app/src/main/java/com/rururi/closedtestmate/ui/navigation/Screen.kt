package com.rururi.closedtestmate.ui.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Login : Screen("login")
    object Signup : Screen("signup")
    object ForgotPassword : Screen("forgot_password")
    object RecruitList : Screen("recruit_list")
    object RecruitNew : Screen("recruit_new")
    object RecruitDetail : Screen("recruit_detail/{recruitId}") {
        fun createRoute(recruitId: String) = "recruit_detail/$recruitId"
    }
}