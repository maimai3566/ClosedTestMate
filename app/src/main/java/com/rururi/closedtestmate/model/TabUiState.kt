package com.rururi.closedtestmate.model

import androidx.annotation.StringRes
import com.rururi.closedtestmate.R

data class TabUiState(
    val selectedTab: TabType = TabType.Tester
)

//TabUiStateで管理する選択肢
sealed class TabType(@StringRes val titleRes: Int) {
    object Tester : TabType(R.string.tab_text_1)
    object QuestionC : TabType(R.string.tab_text_2)
    object Release : TabType(R.string.tab_text_3)

    companion object {
        val allTabs = listOf(Tester, QuestionC, Release)
    }
}
