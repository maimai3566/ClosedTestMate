package com.rururi.closedtestmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rururi.closedtestmate.ui.TabListViewModel
import com.rururi.closedtestmate.ui.login.LoginScreen
import com.rururi.closedtestmate.ui.recruitdetail.RecruitDetailScreen
import com.rururi.closedtestmate.ui.recruitlist.RecruitListScreen
import com.rururi.closedtestmate.ui.recruitnew.RecruitNewScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object RecruitList : Screen("recruit_list")
    object RecruitNew : Screen("recruit_new")
    object RecruitDetail : Screen("recruit_detail/{recruitId}") {
        fun createRoute(recruitId: String) = "recruit_detail/$recruitId"
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginClick = { navController.navigate(Screen.RecruitList.route) })
        }
        composable(Screen.RecruitList.route) {
            RecruitListScreen()
        }
        composable(Screen.RecruitNew.route) {
            RecruitNewScreen()
        }
        composable(
            route = Screen.RecruitDetail.route,
            arguments = listOf(navArgument("recruitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recruitId = backStackEntry.arguments?.getString("recruitId") ?:""
            RecruitDetailScreen(recruitId = recruitId)
        }
    }
}