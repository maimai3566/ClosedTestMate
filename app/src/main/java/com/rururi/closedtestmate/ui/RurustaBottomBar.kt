package com.rururi.closedtestmate.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.ui.navigation.Screen

@Composable
fun RurustaBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isLoggedIn: Boolean,
) {
    //現在のルートを取得
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //
    fun navigateSingleTopTo(route: String) {
        if(currentRoute == route) return    //同じタブなら何もしない
        navController.navigate(route) {
            //ボトムバー定石オプション（重複スタック防止）
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavigationBar {
        //Home
        NavigationBarItem(
            selected = currentRoute == Screen.RecruitList.route,
            onClick = { navigateSingleTopTo(Screen.RecruitList.route) },
            icon = { Icon(
                imageVector = Icons.Default.Home,
                contentDescription = stringResource(R.string.icon_home),
                modifier = Modifier.size(dimensionResource(R.dimen.icon_small))) },
            label = { Text(text = stringResource(R.string.icon_home),maxLines = 1) },
            alwaysShowLabel = true
        )
        //ログインしているとき
        if (isLoggedIn) {
            //検索
            NavigationBarItem(
                selected = currentRoute == Screen.Search.route,
                onClick = { navigateSingleTopTo(Screen.Search.route) },
                icon = { Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.icon_search),
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_small))) },
                label = { Text(text = stringResource(R.string.icon_search),maxLines = 1) },
                alwaysShowLabel = true
            )
            //お気に入り
            NavigationBarItem(
                selected = currentRoute == Screen.Favorite.route,
                onClick = { navigateSingleTopTo(Screen.Favorite.route) },
                icon = { Icon(
                    Icons.Default.Favorite,
                    contentDescription = stringResource(R.string.icon_favorite),
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_small))) },
                label = { Text(text = stringResource(R.string.icon_favorite),maxLines = 1) },
                alwaysShowLabel = true
            )
            //プロフィール
            NavigationBarItem(
                selected = currentRoute == Screen.Profile.route,
                onClick = { navigateSingleTopTo(Screen.Profile.route) },
                icon = { Icon(
                    Icons.Default.Person,
                    contentDescription = stringResource(R.string.icon_settings),
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_small))) },
                label = { Text(text = stringResource(R.string.icon_settings),maxLines = 1) },
                alwaysShowLabel = true
            )
        } else {    //未ログイン
            //ログイン
            NavigationBarItem(
                selected = currentRoute == Screen.Login.route,
                onClick = { navigateSingleTopTo(Screen.Login.route) },
                icon = { Icon(
                    Icons.Default.Login,
                    contentDescription = stringResource(R.string.login),
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_small))) },
                label = { Text(text = stringResource(R.string.login),maxLines = 1) },
                alwaysShowLabel = true
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun RurustaBottomBarPreview() {
    val navController = rememberNavController()
    RurustaBottomBar(navController = navController, isLoggedIn = true)
}