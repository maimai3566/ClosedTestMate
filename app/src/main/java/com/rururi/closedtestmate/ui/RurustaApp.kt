package com.rururi.closedtestmate.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    val hideTopBarRoutes = listOf(Screen.Login.route, Screen.Signup.route)

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
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}