package com.rururi.closedtestmate.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.ui.anime.SlideMessage
import com.rururi.closedtestmate.ui.navigation.AppNavGraph
import com.rururi.closedtestmate.ui.navigation.AuthViewModel
import com.rururi.closedtestmate.ui.navigation.Screen

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun RurustaApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hideTopBarRoutes = listOf(Screen.Login.route, Screen.Signup.route, Screen.ForgotPassword.route)
    val snackbarHostState = remember { SnackbarHostState() }

    val msg = stringResource(R.string.msg_login_required)   //ログインするよう促すメッセージ
    var showLoginMessage by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (currentRoute !in hideTopBarRoutes) {    //authルートにいるときはトップバーを表示させない
                RurustaTopBar(title = stringResource(R.string.app_name))
            }
        },
        bottomBar = {
            if (currentRoute != Screen.Login.route) {
                // TODO: Implement BottomBar
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (currentRoute == Screen.RecruitList.route) {
                FloatingActionButton(
                    onClick = {
                        //アニメーションが始まる前にバックスタックを保存しておく
                        navController.currentBackStackEntry     //バックスタックに
                            ?.savedStateHandle                  //ハンドルの状態を保存し
                            ?.set("redirect", Screen.RecruitNew.route)  //ログイン後のリダイレクト先セットしてから
                        showLoginMessage = true //バックスタックの保存が終わってからアニメ開始
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

            if (showLoginMessage) {
                SlideMessage(
                    message = msg,                  //メッセージをアニメさせる
                    onAnimationEnd = {
                        showLoginMessage = false    //アニメのフラグを切って
                        navController.navigate(Screen.Login.route)  //画面遷移
                    }
                )
            }
        }
    }
}