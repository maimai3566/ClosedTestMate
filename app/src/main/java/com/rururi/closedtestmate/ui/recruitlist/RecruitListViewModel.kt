package com.rururi.closedtestmate.ui.recruitlist

import android.util.Log
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rururi.closedtestmate.model.RecruitStatus
import com.rururi.closedtestmate.model.RecruitUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.core.net.toUri
import com.rururi.closedtestmate.ui.components.toRecruitUiState

@HiltViewModel
class RecruitListViewModel @Inject constructor() : ViewModel() {
    private val _recruitList = MutableStateFlow<List<RecruitUiState>>(emptyList())
    val recruitList: StateFlow<List<RecruitUiState>> = _recruitList.asStateFlow()

    init {
        loadRecruitList()   //インスタンスが作成されたときに１回だけ処理を実行
    }

    fun loadRecruitList() {
        Firebase.firestore.collection("recruit")        //Firestoreのrecruitコレクションにアクセス
            .orderBy("postedAt",Query.Direction.DESCENDING) //投稿日時の新しい順で並べ替え
            .get()                                      //firesotreから全データを取得（非同期）
            .addOnSuccessListener { result ->           //取得が成功したら
                val list = result.mapNotNull { doc ->   //結果（DocumentSnapshot型）を取り出し
                    doc.toRecruitUiState()              //RecruitUiState型に変換
                }
                _recruitList.value = list               //上記リストをStateFlowに設定
            }
    }
}