package com.rururi.closedtestmate.ui.navigation

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
import com.rururi.closedtestmate.ui.login.SignupScreen
import androidx.navigation.compose.navigation
import com.google.firebase.auth.FirebaseAuth
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.model.RecruitUiState
import com.rururi.closedtestmate.ui.anime.SlideMessage
import com.rururi.closedtestmate.ui.login.ForgotPasswordScreen
import com.rururi.closedtestmate.ui.recruitnew.RecruitNewViewModel
import androidx.core.net.toUri
import com.rururi.closedtestmate.model.SaveStatus
import com.rururi.closedtestmate.ui.recruitlist.RecruitListViewModel

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
    val authViewModel: AuthViewModel = hiltViewModel()
    val uiState by authViewModel.uiState.collectAsState()

    //セッションを確認して初期表示する画面を制御
    LaunchedEffect(Unit) {
        if(uiState.isLoggedIn) {  //ログインのセッションがあれば
            navController.navigate(Screen.RecruitList.route)
        } else {    //ログインのセッションがなければ
            navController.navigate(Screen.Auth.route)
        }
    }

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
        //テスター募集一覧
        composable(Screen.RecruitList.route) {
            val viewModel:RecruitListViewModel = hiltViewModel()
            val recruitList by viewModel.recruitList.collectAsState()

            RecruitListScreen(
                recruitList = recruitList,
            )
        }
        //テスター募集新規登録
        composable(Screen.RecruitNew.route) {
            val viewModel: RecruitNewViewModel = hiltViewModel()
            val uiState: RecruitUiState by viewModel.uiState.collectAsState()

            RecruitNewScreen(
                uiState = uiState,
                navController = navController,
                onStatusChange = { viewModel.updateUiState { copy(status = it) } },
                onAppIconChange = { uri ->
                    viewModel.updateUiState { copy(appIcon = uri) }
                },
                onAppNameChange = { viewModel.updateUiState { copy(appName = it) } },
                onDescriptionChange = { viewModel.updateUiState { copy(description = it) } },
                onGroupUrlChange = { viewModel.updateUiState { copy(groupUrl = it) } },
                onAppUrlChange = { viewModel.updateUiState { copy(appUrl = it) } },
                onWebUrlChange = { viewModel.updateUiState { copy(webUrl = it) } },
                onSaveClick = { viewModel.saveRecruit() },
                onClearClick = { viewModel.clear() },
                onMsgEnd = { viewModel.onMsgEnd() },
            )
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