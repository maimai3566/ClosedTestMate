package com.rururi.closedtestmate.ui.recruitnew

import android.R.attr.text
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.common.collect.Multimaps.index
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.rururi.closedtestmate.model.DetailContent
import com.rururi.closedtestmate.model.RecruitStatus
import com.rururi.closedtestmate.model.RecruitUiState
import com.rururi.closedtestmate.ui.components.toMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RecruitNewViewModel @Inject constructor() : ViewModel() {
    //投稿状態の保持
    private val _uiState = MutableStateFlow(RecruitUiState(details = listOf(DetailContent.Text())))
    val uiState: StateFlow<RecruitUiState> = _uiState.asStateFlow()

    //初期化
    init {
        initDetails()
    }

    //汎用の更新関数
    fun updateUiState(update: RecruitUiState.() -> RecruitUiState) {
        _uiState.update (update)
    }

    //投稿を保存
    fun saveRecruit() {
        //FirebaseのFirestoreに保存する
        val recruitData = hashMapOf(
            "appName" to uiState.value.appName,
            "appIcon" to (uiState.value.appIcon?.toString() ?:"") ,  //Uriを文字列に変換
            "status" to uiState.value.status.fireStoreLabel,   //募集中・募集終了とか
            "details" to uiState.value.details.map { it.toMap() },  //詳細項目をマップ型に変換してリストに追加
            "groupUrl" to uiState.value.groupUrl,
            "appUrl" to uiState.value.appUrl,
            "webUrl" to uiState.value.webUrl,
            "postedAt" to System.currentTimeMillis()
        )
        Log.d("ruruV","Firestoreに保存します:$recruitData")
        Firebase.firestore.collection("recruit")
            .add(recruitData)
            .addOnSuccessListener { documentRef ->
                _uiState.update {
                    it.copy(id = documentRef.id, isSaved = true)
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isSaved = false) }
            }
    }

    //スライドメッセージの表示終了
    fun onMsgEnd() {
        _uiState.update { it.copy(isSaved = false) } //画面遷移してOK
    }

    //入力値をクリア
    fun clear() {
        _uiState.update {
            it.copy(
                status = RecruitStatus.Closed,
                appIcon = null,
                appName = "",
                details = nonEmptyDetails(it.details),
                groupUrl = "",
                appUrl = "",
                webUrl = "",
                isSaved = false
            )
        }
    }

    /**－－－－－details　API　ここから！－－－－－**/
    //EmptyListを許さないための処理
    private fun nonEmptyDetails(list: List<DetailContent>): List<DetailContent> =
        if(list.isEmpty()) listOf(DetailContent.Text()) else list

    //初期化
    fun initDetails() {
        _uiState.update { state ->
            //detailsが空リストだったらテキストがブランクのリストを作る、空でなければなにもしない
            state.copy(details = nonEmptyDetails(state.details))
        }
    }

    //募集内容にテキストフィールドを追加（ブランクのTextをdetailに追加することによりテキストフィールドがつかされる）
    fun addDetailText() {
        _uiState.update { state ->
            state.copy(details = state.details + DetailContent.Text(text = ""))
        }
    }

    //募集内容に画像を追加
    fun addDetailImage(uri: Uri) {
        _uiState.update { state ->
            state.copy(details = state.details + DetailContent.Image(uri= uri))
        }
    }

    //募集内容のテキスト部分を更新
    fun updateDetailText(id:String, text:String) {
        _uiState.update { state ->
            //mapを使う理由
            val updated = state.details.map {   //既存のList<DetailContent>を1件ずつ変換して新しいListにする
                if (it.id == id && it is DetailContent.Text) it.copy(text = text) else it   //同じIDの場所だけTextを差し替え
            }
            state.copy(details = updated)   //新リストを更新
        }
    }

    //IDを削除
    fun removeById(id:String) {
        _uiState.update { state ->
            val after = state.details.filterNot{ it.id == id }  //同じidを持つものを除外
            state.copy(details = nonEmptyDetails(after))
        }
    }
    /**APIここまで**/
}