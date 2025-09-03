package com.rururi.closedtestmate.ui

import androidx.lifecycle.ViewModel
import com.rururi.closedtestmate.core.model.TabType
import com.rururi.closedtestmate.core.model.TabUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class TabListViewModel @Inject constructor():ViewModel()  {
    private val _uiState = MutableStateFlow(TabUiState())
    val uiState: StateFlow<TabUiState> = _uiState.asStateFlow()

    //Tabを変更
    fun updateTab(selectedTab: TabType) {
        _uiState.update {
            it.copy(
                selectedTab = selectedTab
            )
        }
    }
}