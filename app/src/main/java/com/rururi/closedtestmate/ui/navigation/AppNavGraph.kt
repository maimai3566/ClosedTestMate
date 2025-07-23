package com.rururi.closedtestmate.ui.navigation

import android.view.Gravity
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rururi.closedtestmate.ui.login.SignupScreen
import androidx.navigation.compose.navigation
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.ui.anime.SlideMessage
import com.rururi.closedtestmate.ui.login.ForgotPasswordScreen

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

@Composable
fun AppNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route,
        modifier = modifier
    ) {
        navigation(
            route = Screen.Auth.route,
            startDestination = Screen.Login.route
        ) {

            //ログイン
            composable(Screen.Login.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.Auth.route) }
                val viewModel:LoginViewModel = hiltViewModel(parentEntry)
                val uiState by viewModel.uiState.collectAsState()

                LoginScreen(
                    uiState = uiState,
                    onMessageReset = { viewModel.updateUiState { copy(success = "", error = "") }},
                    onEmailChange = { viewModel.updateUiState { copy(email = it) } },
                    onPasswordChange = { viewModel.updateUiState { copy(pw = it) } },
                    onLogin = {
                        viewModel.login(
                            onSuccess = {
                                navController.navigate(Screen.RecruitList.route)
                            },
                        )
                    },
                    onForgotPw = {
                        viewModel.resetUiState()    //状態をリセットする
                        navController.navigate(Screen.ForgotPassword.route)
                    },
                    onSignup = {
                        viewModel.resetUiState()    //状態をリセットする
                        navController.navigate(Screen.Signup.route)
                    },
                    onSkipLogin = { navController.navigate(Screen.RecruitList.route) }  //そのまま募集一覧へ
                )
            }
            //サインアップ
            composable(Screen.Signup.route){ backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.Auth.route) }
                val viewModel:LoginViewModel = hiltViewModel(parentEntry)
                val uiState by viewModel.uiState.collectAsState()

                SignupScreen(
                    navController = navController,
                    onMessageReset = { viewModel.updateUiState { copy(success = "", error = "") }},
                    onEmailChange = { viewModel.updateUiState { copy(email = it) } },
                    onPasswordChange = { viewModel.updateUiState { copy(pw = it) } },
                    onConfirmPasswordChange = { viewModel.updateUiState { copy(pw2 = it) } },
                    onSignUpClick = { viewModel.signUp(onSuccess = {}) },
                    snackbarHostState = snackbarHostState,
                    uiState = uiState
                )
            }
            //パスワードリセット画面
            composable(Screen.ForgotPassword.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.Auth.route) }
                val viewModel:LoginViewModel = hiltViewModel(parentEntry)
                val uiState by viewModel.uiState.collectAsState()

                ForgotPasswordScreen(
                    navController = navController,
                    onMessageReset = { viewModel.updateUiState { copy(success = "", error = "") }},
                    onEmailChange = { viewModel.updateUiState { copy(email = it) } },
                    onForgotPw = {
                        viewModel.forgotPassword(
                            onSuccess = { navController.navigate(Screen.Login.route) }
                        )
                    },
                    uiState = uiState
                )
            }
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