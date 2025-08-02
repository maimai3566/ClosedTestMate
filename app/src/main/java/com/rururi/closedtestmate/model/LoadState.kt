package com.rururi.closedtestmate.model

sealed class LoadState {
    object Loading : LoadState()    //Loadingという状態のみ
    object Error : LoadState()
    data class Success(val recruit: RecruitUiState) : LoadState()   //成功の場合はrecruitをセット
}