package com.rururi.closedtestmate.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.ui.navigation.AppNavGraph
import com.rururi.closedtestmate.ui.navigation.Screen

@Composable
fun RurustaApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val hideTopBarRoutes = listOf(Screen.Login.route, Screen.Signup.route, Screen.ForgotPassword.route)
    val snackbarHostState = remember { SnackbarHostState() }

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
                FloatingActionButton(onClick = { navController.navigate(Screen.RecruitNew.route) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.button_recruit_add)
                    )
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            snackbarHostState = snackbarHostState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}