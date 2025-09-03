package com.rururi.closedtestmate.app.navigation

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.rururi.closedtestmate.auth.ui.forgot.ForgotScreen
import com.rururi.closedtestmate.auth.ui.forgot.ForgotViewModel
import com.rururi.closedtestmate.auth.ui.login.LoginScreen
import com.rururi.closedtestmate.auth.ui.login.LoginViewModel
import com.rururi.closedtestmate.auth.ui.sighnup.SignupScreen
import com.rururi.closedtestmate.auth.ui.sighnup.SignupViewModel
import com.rururi.closedtestmate.ui.profile.ProfileScreen
import com.rururi.closedtestmate.ui.profile.ProfileViewModel
import com.rururi.closedtestmate.ui.recruitdetail.RecruitDetailScreen
import com.rururi.closedtestmate.ui.recruitdetail.RecruitDetailUiState
import com.rururi.closedtestmate.ui.recruitdetail.RecruitDetailViewModel
import com.rururi.closedtestmate.ui.recruitlist.RecruitListScreen
import com.rururi.closedtestmate.ui.recruitlist.RecruitListViewModel
import com.rururi.closedtestmate.ui.recruitnew.RecruitNewScreen
import com.rururi.closedtestmate.ui.recruitnew.RecruitNewUiState
import com.rururi.closedtestmate.ui.recruitnew.RecruitNewViewModel

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
            val uiState by viewModel.uiState.collectAsState()

            RecruitListScreen(
                uiState = uiState,
                onClickRecruit = {
                    navController.navigate(Screen.RecruitDetail.createRoute(it))
                }
            )
        }

        //テスター募集新規登録
        composable(
            route = Screen.RecruitNew.route,
            arguments = listOf(
                navArgument("status") { type = NavType.StringType },
                navArgument("appName") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val viewModel: RecruitNewViewModel = hiltViewModel(backStackEntry)
            val uiState: RecruitNewUiState by viewModel.uiState.collectAsState()

            //画像ピッカー
            val pickImage = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ){ uri ->
                if(uri != null) viewModel.addDetailImage(uri)
            }

            RecruitNewScreen(
                uiState = uiState,
                eventFlow = viewModel.event,
                navController = navController,
                //入力
                onStatusChange = { viewModel.updateInput { copy(status = it) } },
                onAppIconChange = { uri ->
                    viewModel.updateInput { copy(appIcon = uri) }
                },
                onAppNameChange = { viewModel.updateInput{ copy(appName = it) } },
                onGroupUrlChange = { viewModel.updateInput { copy(groupUrl = it) } },
                onAppUrlChange = { viewModel.updateInput { copy(appUrl = it) } },
                onWebUrlChange = { viewModel.updateInput { copy(webUrl = it) } },
                //募集内容（detail）
                onDetailTextChange = viewModel::updateDetailText,
                onDetailTextAdd = { viewModel.addDetailText() },
                onDetailImageAdd = { pickImage.launch("image/*") },
                onDetailRemove = { viewModel.removeById(it) },
                //投稿
                onSaveClick = { viewModel.saveRecruit() },
                onClearClick = { viewModel.clear() },
                onMsgEnd = { viewModel.onMsgEnd() },
            )
        }
        //詳細画面
        composable(
            route = Screen.RecruitDetail.route,
            arguments = listOf(navArgument("recruitId") { type = NavType.StringType })
        ) { backStackEntry ->
            //SavedStateHandleにはbackstackEntryで取得したrecruitIdがはいる
            val viewModel: RecruitDetailViewModel = hiltViewModel(backStackEntry)
            val uiState: RecruitDetailUiState by viewModel.uiState.collectAsState()

            RecruitDetailScreen(
                uiState = uiState,
            )
        }

        //profile画面
        composable(Screen.Profile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            ProfileScreen(
                uiState = uiState,
                onImageSelected = { viewModel.updateUiState { copy(session = session?.copy(photoUrl = it)) } },
                onShowEdit = { viewModel.startEdit() },
                onMyRecruitClick = { /*TODO*/ },
                onFavoriteClick = { /*TODO*/ },
                onLogoutClick = { viewModel.logout() },
                onNameChange = { viewModel.onChangeName(it) },
                onDismiss = { viewModel.dismissEdit() },
                onConfirm = { viewModel.saveName() },
            )
        }
        //search画面
        composable(Screen.Search.route) {
//            SearchScreen()
        }
        //favorite画面
        composable(Screen.Favorite.route) {
//            FavoriteScreen()
        }
    }
}