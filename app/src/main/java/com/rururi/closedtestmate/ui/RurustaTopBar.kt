package com.rururi.closedtestmate.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.model.TabType
import com.rururi.closedtestmate.model.TabUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RurustaTopBar(title:String) {
    val tabTitles = TabType.allTabs.map { stringResource(it.titleRes) }
    val viewModel: TabListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val selectedTabIndex = TabType.allTabs.indexOf(uiState.selectedTab) // 選択されたタブのインデックスを取得

    Column() {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        )
        TabRow(selectedTabIndex = selectedTabIndex){
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab.titleRes == index,
                    onClick = { viewModel.updateTab(TabType.allTabs[index])  },
                    text = { Text(text = title) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    RurustaTopBar(title = stringResource(R.string.app_name))
}