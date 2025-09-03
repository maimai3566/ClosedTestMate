package com.rururi.closedtestmate.ui.recruitlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rururi.closedtestmate.recruit.domain.RecruitRepository
import com.rururi.closedtestmate.recruit.ui.toListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RecruitListViewModel @Inject constructor(
    private val repo: RecruitRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecruitListUiState(isLoading = true))
    val uiState: StateFlow<RecruitListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init { loadRecruitList() }

    //Firestoreから募集一覧を取得
    fun loadRecruitList(limit: Int = 50) {
        loadJob?.cancel()   //前のFlowをキャンセル(多重購読禁止)
        loadJob = repo.getList(limit)
            .map { list -> list.map { it.toListItem() } }   //Domain -> Ui
            .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }    //list読み込み開始
            .onEach { list ->   //正常時
                _uiState.update { it.copy(isLoading = false, recruitList = list, error = null) }
            }
            .catch { e ->   //エラー
                _uiState.update { it.copy(isLoading = false, recruitList = emptyList(), error = e as? Exception) }
            }
            .launchIn(viewModelScope)   //Flowをコルーチンに登録
    }
}