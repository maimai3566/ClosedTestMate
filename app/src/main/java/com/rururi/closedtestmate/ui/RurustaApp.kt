package com.rururi.closedtestmate.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.ui.anime.SlideMessage
import com.rururi.closedtestmate.ui.navigation.AppNavGraph
import com.rururi.closedtestmate.auth.AuthViewModel
import com.rururi.closedtestmate.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun RurustaApp(onExit : () -> Unit) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideTopBarRoutes = listOf(Screen.Login.route, Screen.Signup.route, Screen.ForgotPassword.route)
    val snackbarHostState = remember { SnackbarHostState() }

    //
    val loginMsg = stringResource(R.string.msg_login_required)   //ログインするよう促すメッセージ
    var showLoginMessage by remember { mutableStateOf(false) }

    val exitMsg = stringResource(R.string.msg_double_back)
    var backArmed by remember { mutableStateOf(false) }
    var showExitMessage by remember { mutableStateOf(false) }   //ダブルバックした時の処理

    //login関係
    val loginSuccessMsg = stringResource(R.string.msg_login_success)
    var prevLoggedIn by remember { mutableStateOf(isLoggedIn)}  //アプリ起動時のログイン状態
    var showLoginSuccess by remember { mutableStateOf(false) }  //ログインしました

    //logout関係
    var showLogoutConfirm by remember { mutableStateOf(false) } //ログアウトの確認
    var showLoggedOutMessage by remember { mutableStateOf(false) }  //ログアウトしました

    //ログイン状態が変化したとき
    LaunchedEffect(isLoggedIn) {    //isLoggedInが変化したときに実行
        if (!prevLoggedIn && isLoggedIn) {   //初期が未ログインで現在がログインの時は
            showLoginSuccess = true       //ログインしましたメッセージを表示する
        }
        prevLoggedIn = isLoggedIn
    }

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
                // TODO: Implement BottomBar
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Log.d("ruruS FAB", "isLoggedIn: $isLoggedIn")
            if (currentRoute == Screen.RecruitList.route) {
                FloatingActionButton(
                    onClick = {
                        if (isLoggedIn) {   //ログイン状態であれば
                            navController.navigate(Screen.RecruitNew.route)
                        } else {            //未ログイン状態であれば
                            //アニメーションが始まる前にバックスタックを保存しておく
                            navController.currentBackStackEntry     //バックスタックに
                                ?.savedStateHandle                  //ハンドルの状態を保存し
                                ?.set("redirect", Screen.RecruitNew.route)  //ログイン後のリダイレクト先セットしてから
                            showLoginMessage = true //バックスタックの保存が終わってからアニメ開始
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
                snackbarHostState = snackbarHostState,
                isLoggedIn = isLoggedIn,
            )
            //ログイン成功のメッセージ
            if (showLoginSuccess) {
                SlideMessage(
                    message = loginSuccessMsg,
                    onAnimationEnd = { showLoginSuccess = false }
                )
            }

            //新規登録時に未ログインの時にログインを促すメッセージ
            if (showLoginMessage) {
                SlideMessage(
                    message = loginMsg,                  //メッセージをアニメさせる
                    onAnimationEnd = {
                        showLoginMessage = false    //アニメのフラグを切って
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