package com.rururi.closedtestmate.ui.navigation

import android.annotation.SuppressLint
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
import com.rururi.closedtestmate.auth.login.LoginScreen
import com.rururi.closedtestmate.ui.recruitdetail.RecruitDetailScreen
import com.rururi.closedtestmate.ui.recruitlist.RecruitListScreen
import com.rururi.closedtestmate.ui.recruitnew.RecruitNewScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rururi.closedtestmate.auth.sighnup.SignupScreen
import androidx.navigation.compose.navigation
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.auth.forgot.ForgotScreen
import com.rururi.closedtestmate.auth.forgot.ForgotViewModel
import com.rururi.closedtestmate.auth.login.LoginViewModel
import com.rururi.closedtestmate.auth.sighnup.SignupViewModel
import com.rururi.closedtestmate.model.LoadState
import com.rururi.closedtestmate.ui.anime.SlideMessage
import com.rururi.closedtestmate.ui.recruitdetail.RecruitDetailViewModel
import com.rururi.closedtestmate.ui.recruitlist.RecruitListViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean,    //受け取るだけ
    modifier: Modifier = Modifier,
) {
    //初期画面(ログインしていれば募集一覧、ログインしていなければログイン)
    val startDest = if (isLoggedIn) Screen.RecruitList.route else Screen.Auth.route

    NavHost(
        navController = navController,
        startDestination = startDest,
        modifier = modifier
    ) {
        //認証ルート
        navigation(
            route = Screen.Auth.route,  //navigationの名前
            startDestination = Screen.Login.route
        ) {
            //ログイン
            composable(Screen.Login.route) { backStackEntry ->
                val loginVM = hiltViewModel<LoginViewModel>(backStackEntry)
                val uiState by loginVM.uiState.collectAsState()

                //login画面表示
                LoginScreen(
                    uiState = uiState,
                    onEmailChange = { loginVM.updateUiState{ copy(email = it) } },
                    onPasswordChange = { loginVM.updateUiState { copy(pw = it) } },
                    onLogin = { loginVM.login(uiState.email, uiState.pw) },
                    onForgotPw = { navController.navigate(Screen.ForgotPassword.route) },
                    onSignUp = { navController.navigate(Screen.Signup.route) },
                    onSkipLogin = {
                        navController.navigate(Screen.RecruitList.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
            //サインアップ
            composable(Screen.Signup.route){ backStackEntry ->
                val signupVM = hiltViewModel<SignupViewModel>()
                val uiState by signupVM.uiState.collectAsState()

                SignupScreen(
                    uiState = uiState,
                    onEmailChange = { signupVM.updateUiState { copy(email = it) }},
                    onPasswordChange = { signupVM.updateUiState { copy(pw = it) } },
                    onConfirmPasswordChange = { signupVM.updateUiState { copy(pw2 = it) } },
                    onSignUpClick = { signupVM.signup(email = uiState.email, pw = uiState.pw) },
                )
            }
            //パスワードリセット画面
            composable(Screen.ForgotPassword.route) { backStackEntry ->
                val forgotVM = hiltViewModel<ForgotViewModel>()
                val uiState by forgotVM.uiState.collectAsState()

                ForgotScreen(
                    uiState = uiState,
                    onEmailChange = { forgotVM.updateUiState { copy(email = it) } },
                    onForgotPw = { forgotVM.forgotPassword(uiState.email) },
                    onResetSuccess = { forgotVM.updateUiState { copy(success = false) } },
                    navToLogin = {
                        navController.popBackStack(
                            route = Screen.Login.route,
                            inclusive = false
                        )
                    }
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