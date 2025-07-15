package com.rururi.closedtestmate.ui.navigation

import android.view.Gravity
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rururi.closedtestmate.ui.TabListViewModel
import com.rururi.closedtestmate.ui.login.LoginScreen
import com.rururi.closedtestmate.ui.login.LoginViewModel
import com.rururi.closedtestmate.ui.recruitdetail.RecruitDetailScreen
import com.rururi.closedtestmate.ui.recruitlist.RecruitListScreen
import com.rururi.closedtestmate.ui.recruitnew.RecruitNewScreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext

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
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            val viewModel:LoginViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            LoginScreen(
                uiState = uiState,
                onEmailChange = { viewModel.updateEmail(it) },
                onPasswordChange = { viewModel.updatePassword(it) },
                onLogin = {
                    viewModel.login(
                        onSuccess = { navController.navigate(Screen.RecruitList.route) },
                        onError = { errorMsg ->
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).apply {
                                setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 300)
                                show()
                            }
                        }
                    )
                },
                onSkipLogin = { navController.navigate(Screen.RecruitList.route) }  //そのまま募集一覧へ
            )
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