package com.rururi.closedtestmate.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.auth.AuthViewModel
import com.rururi.closedtestmate.ui.anime.SlideMessage
import com.rururi.closedtestmate.ui.navigation.AppNavGraph
import com.rururi.closedtestmate.ui.navigation.Screen
import kotlinx.coroutines.delay

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun RurustaApp(onExit : () -> Unit) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideTopBarRoutes = listOf(Screen.Login.route, Screen.Signup.route, Screen.ForgotPassword.route)

    //未ログインでFABをクリックしたとき
    var showLoginRequired by remember { mutableStateOf(false) } //ログインを促すメッセージ

    //home(一覧画面）にいるときにバックをタップしたとき
    val exitMsg = stringResource(R.string.msg_double_back)
    var backArmed by remember { mutableStateOf(false) }
    var showExitMessage by remember { mutableStateOf(false) }   //ダブルバックした時の処理

    //logout関係
    var showLogoutConfirm by remember { mutableStateOf(false) } //ログアウトの確認
    var showLoggedOutMessage by remember { mutableStateOf(false) }  //ログアウトしました

    //backArmedは2秒で解除
    LaunchedEffect(backArmed) {
        if (backArmed) {
            delay(2000)
            backArmed = false
        }
    }
    //homeにいるときだけダブルバックを有効とする
    BackHandler(enabled = currentRoute == Screen.RecruitList.route) {
        if (backArmed) {
            onExit()
        } else {
            backArmed = true
            showExitMessage = true
        }
    }

    //login成功メッセージ
    var showLoginSuccessMsg by remember { mutableStateOf(false) }
    LaunchedEffect(isLoggedIn,currentRoute) {
        if (isLoggedIn && currentRoute == Screen.Login.route) {
            showLoginSuccessMsg = true  //SlideMessageでアニメーションさせる
        }
    }

    //サインアップ成功メッセージ
    var showSignupSuccessMsg by remember { mutableStateOf(false) }
    LaunchedEffect(isLoggedIn,currentRoute) {
        if (isLoggedIn && currentRoute == Screen.Signup.route) {
            showSignupSuccessMsg = true  //SlideMessageでアニメーションさせる
        }
    }

    Scaffold(
        topBar = {
            if (currentRoute !in hideTopBarRoutes) {    //authルートにいるときはトップバーを表示させない
                RurustaTopBar(
                    title = stringResource(R.string.app_name),
                    actions = {
                        if (isLoggedIn) {
                            IconButton(
                                onClick = { showLogoutConfirm = true }  //ログアウト確認
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = stringResource(R.string.logout)
                                )
                            }

                        }
                    }
                )
            }
        },
        bottomBar = {
            if (currentRoute != Screen.Login.route) {
                RurustaBottomBar(navController = navController, isLoggedIn = isLoggedIn)
            }
        },
        floatingActionButton = {
            Log.d("ruruS FAB", "isLoggedIn: $isLoggedIn")
            if (currentRoute == Screen.RecruitList.route) {
                FloatingActionButton(
                    onClick = {
                        if (isLoggedIn) {   //ログイン状態であれば
                            navController.navigate(Screen.RecruitNew.route)
                        } else {            //未ログイン状態であれば
                            showLoginRequired = true //スライドメッセージ
                            navController.navigate(Screen.Login.route) {    //メッセージが終わってからログインへ
                                launchSingleTop = true
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.button_recruit_add)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavGraph(
                navController = navController,
                isLoggedIn = isLoggedIn,
            )

            //サインアップ成功時のメッセージ
            if (showSignupSuccessMsg) {
                SlideMessage(
                    message = stringResource(R.string.msg_signup_success),
                    onAnimationEnd = {
                        showSignupSuccessMsg = false
                        navController.navigate(Screen.RecruitList.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            //ログイン成功時のメッセージ
            if (showLoginSuccessMsg) {
                SlideMessage(
                    message = stringResource(R.string.msg_login_success),
                    onAnimationEnd = {
                        showLoginSuccessMsg = false
                        navController.navigate(Screen.RecruitList.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            //新規登録時に未ログインの時にログインを促すメッセージ
            if (showLoginRequired) {
                SlideMessage(
                    message = stringResource(R.string.msg_login_required),                  //メッセージをアニメさせる
                    onAnimationEnd = {
                        showLoginRequired = false    //アニメのフラグを切って
                        navController.navigate(Screen.Login.route)  //画面遷移
                    }
                )
            }
            //アプリ終了
            if(showExitMessage) {
                SlideMessage(
                    message = exitMsg,
                    onAnimationEnd = { showExitMessage = false }
                )
            }
            //ログアウト確認
            if(showLogoutConfirm) {
                AlertDialog(
                    onDismissRequest = { showLogoutConfirm = false },
                    title = { Text(text = stringResource(R.string.logout)) },
                    text = { Text(text = stringResource(R.string.logout_confirm),style = MaterialTheme.typography.bodyLarge) },
                    confirmButton = {
                        Button(
                            onClick = {
                                authViewModel.logout()
                                showLogoutConfirm = false
                                showLoggedOutMessage = true
                            }
                        ) {
                            Text(text = stringResource(R.string.logout))
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showLogoutConfirm = false }
                        ) {
                            Text(text = stringResource(R.string.cancel))
                        }
                    }
                )
            }
            //ログアウトメッセージ
            if(showLoggedOutMessage) {
                SlideMessage(
                    message = stringResource(R.string.msg_logout),
                    onAnimationEnd = { showLoggedOutMessage = false }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RurustaAppPreview() {
    RurustaApp(onExit = {})
}