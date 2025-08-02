package com.rururi.closedtestmate.ui.recruitdetail

import android.util.Log
import android.util.Log.e
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rururi.closedtestmate.model.LoadState
import com.rururi.closedtestmate.model.RecruitUiState
import com.rururi.closedtestmate.ui.components.toRecruitUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RecruitDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
):ViewModel() {
    private val recruitId: String = checkNotNull(savedStateHandle["recruitId"])

    private val _uiState = MutableStateFlow<LoadState>(LoadState.Loading)
    val uiState: StateFlow<LoadState> = _uiState.asStateFlow()

    init {
        loadRecruitDetail()
    }
    private fun loadRecruitDetail() {
        //recruitIdに対応したデータをFirebaseから読み込む
        Firebase.firestore.collection("recruit")    //Firestoreのrecruitコレクションにアクセス
            .document(recruitId)                    //ドキュメントIDで1件取得
            .get()                                  //データ取得
            .addOnSuccessListener { result ->       //成功したときの処理
                result.toRecruitUiState()?.let{
                    _uiState.value = LoadState.Success(it)
                } ?: run {
                    _uiState.value = LoadState.Error
                }
            }
            .addOnFailureListener { e ->            //ロードが失敗したときの処理
                _uiState.value = LoadState.Error
            }
    }
}