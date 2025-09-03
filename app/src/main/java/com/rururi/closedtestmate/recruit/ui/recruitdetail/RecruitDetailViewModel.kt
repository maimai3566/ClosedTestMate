package com.rururi.closedtestmate.ui.recruitdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rururi.closedtestmate.recruit.domain.RecruitRepository
import com.rururi.closedtestmate.recruit.ui.toDetailItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecruitDetailViewModel @Inject constructor(
    private val repo: RecruitRepository,
    savedStateHandle: SavedStateHandle
):ViewModel() {
    private val _uiState = MutableStateFlow<RecruitDetailUiState>(RecruitDetailUiState(isLoading = true))
    val uiState: StateFlow<RecruitDetailUiState> = _uiState.asStateFlow()

    private val recruitId:String =
        checkNotNull(savedStateHandle["recruitId"]) {
            "recruitId(nav arg 'id') is required"
        }

    private var loadJob: Job? = null

    init { loadRecruitDetail() }
    fun loadRecruitDetail() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null)}  //ロード開始
            runCatching { repo.getById(recruitId) }
                .map { it?.toDetailItem() }
                .onSuccess { item ->
                    _uiState.update { it.copy(isLoading = false, detail = item, error = null) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, detail = null, error = e as? Exception) }
                }
        }
    }
}