package com.rururi.closedtestmate.ui.navigation

import android.annotation.SuppressLint
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rururi.closedtestmate.model.LoadState
import com.rururi.closedtestmate.model.SaveStatus
import com.rururi.closedtestmate.ui.recruitdetail.RecruitDetailViewModel
import com.rururi.closedtestmate.ui.recruitlist.RecruitListViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    isLoggedIn: Boolean,    //受け取るだけ
    modifier: Modifier = Modifier,
) {
    //初期画面(ログインしていれば募集一覧、ログインしていなければログイン)
    val startDest = if (isLoggedIn) Screen.RecruitList.route else Screen.Auth.route

    NavHost(
        navController = navController,
        startDestination = startDest,
        route = "root",     //navHostの名前
        modifier = modifier
    ) {
        //認証ルート
        navigation(
            route = Screen.Auth.route,  //navigationの名前
            startDestination = Screen.Login.route
        ) {
            //ログイン
            composable(Screen.Login.route) { backStackEntry ->
                //Login用VM
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.Auth.route) }
                val viewModel:LoginViewModel = hiltViewModel(parentEntry)
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(uiState.success) {
                    if(uiState.success.isNotBlank()) {
                        //リダイレクト用
                        val redirect = navController.previousBackStackEntry
                            ?.savedStateHandle          //リダイレクト先が保存されていれば
                            ?.get<String>("redirect")   //保存した先に飛ぶ
                            ?: Screen.RecruitList.route //リダイレクト先がnullなら募集一覧に飛ぶ

                        navController.previousBackStackEntry    //一度使ったバックスタックエントリは
                            ?.savedStateHandle
                            ?.remove<String>("redirect")    //消す
                        navController.navigate(redirect) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                        //successを消す
                        viewModel.updateUiState { copy(success = "") }
                    }
                }

                LoginScreen(
                    uiState = uiState,
                    onMessageReset = { viewModel.updateUiState { copy(success = "", error = "") }},
                    onEmailChange = { viewModel.updateUiState { copy(email = it) } },
                    onPasswordChange = { viewModel.updateUiState { copy(pw = it) } },
                    onLogin = { viewModel.login() },    //ログイン処理
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
                onClickRecruit = {
                    navController.navigate(Screen.RecruitDetail.createRoute(it))
                }
            )
        }
        //テスター募集新規登録
        composable(Screen.RecruitNew.route) {
            RecruitNewScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
//            val viewModel: RecruitNewViewModel = hiltViewModel()
//            val uiState: RecruitUiState by viewModel.uiState.collectAsState()
//
//            RecruitNewScreen(
//                uiState = uiState,
//                navController = navController,
//                onStatusChange = { viewModel.updateUiState { copy(status = it) } },
//                onAppIconChange = { uri ->
//                    viewModel.updateUiState { copy(appIcon = uri) }
//                },
//                onAppNameChange = { viewModel.updateUiState { copy(appName = it) } },
//                onDetailChange = { viewModel.updateDetailText() },  //todo:後で修正
//                onGroupUrlChange = { viewModel.updateUiState { copy(groupUrl = it) } },
//                onAppUrlChange = { viewModel.updateUiState { copy(appUrl = it) } },
//                onWebUrlChange = { viewModel.updateUiState { copy(webUrl = it) } },
//                onSaveClick = { viewModel.saveRecruit() },
//                onClearClick = { viewModel.clear() },
//                onMsgEnd = { viewModel.onMsgEnd() },
//            )
        }
        //詳細画面
        composable(
            route = Screen.RecruitDetail.route,
            arguments = listOf(navArgument("recruitId") { type = NavType.StringType })
        ) { backStackEntry ->
            val viewModel: RecruitDetailViewModel = hiltViewModel()
            val uiState: LoadState by viewModel.uiState.collectAsState()
            val recruitId = backStackEntry.arguments?.getString("recruitId") ?:""

            RecruitDetailScreen(
                recruitId = recruitId,
                uiState = uiState,
            )
        }
    }
}