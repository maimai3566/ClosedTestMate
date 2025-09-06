package com.rururi.closedtestmate.ui.recruitnew

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rururi.closedtestmate.auth.domain.AuthRepository
import com.rururi.closedtestmate.recruit.domain.DetailContent
import com.rururi.closedtestmate.recruit.domain.RecruitRepository
import com.rururi.closedtestmate.recruit.domain.RecruitStatus
import com.rururi.closedtestmate.recruit.ui.recruitnew.RecruitNewEvent
import com.rururi.closedtestmate.recruit.ui.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecruitNewViewModel @Inject constructor(
    private val repo: RecruitRepository,
    private val auth: AuthRepository
) : ViewModel() {
    //投稿状態の保持
    private val _uiState = MutableStateFlow(RecruitNewUiState())
    val uiState: StateFlow<RecruitNewUiState> = _uiState.asStateFlow()

    //イベント用
    private val _event = MutableSharedFlow<RecruitNewEvent?>()
    val event: SharedFlow<RecruitNewEvent?> = _event.asSharedFlow()

    //初期化
    init { initDetails() }

    //汎用の更新関数
    fun updateUiState(update: RecruitNewUiState.() -> RecruitNewUiState) {
        _uiState.update (update)
    }

    //入れ子のinputを更新するヘルパー関数
    fun updateInput(update: RecruitNewItem.() -> RecruitNewItem) {
        _uiState.update { it.copy(input = it.input.update()) }
    }
    
    //投稿を保存
    fun saveRecruit() {
        val input = _uiState.value.input
        //ヴァリデーションチェック
        if (!input.isValid) {
            _uiState.update { it.copy(isSaving = false) }
            return
        }
        //ユーザが取得できなければ処理終わり
        val user = auth.currentUser()   //リポジトリからユーザ情報を取得
        if (user == null) {
            _uiState.update { it.copy(isSaving = false) }
            return
        }

        //取得できたユーザ情報と入力情報をdomainに突っ込む
        val domain = input.toDomain(
            authorId = user.uid,
            authorName = user.name ?:"",
            authorIcon = user.photoUrl
        )
        Log.d("ruruv", "saveRecruit: $domain")
        //FirebaseのFirestoreに保存する
        _uiState.update { it.copy(isSaving = true) } //保存中にする
        viewModelScope.launch {
            try {
                val docId = repo.add(domain)
                Log.d("ruruv", "saveRecruit: $docId")

                _uiState.update { it.copy(isSaving = false) }
                _event.emit(RecruitNewEvent.SaveSucceeded)  //イベントに保存成功を送信
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                _event.emit(RecruitNewEvent.SaveFailed)     //イベントに保存失敗を送信
            }
        }
    }

    //スライドメッセージの表示終了
//    fun onMsgEnd() {
//        _uiState.update { it.copy(isSaving = false) }   //画面遷移してOK
//    }

    //入力値をクリア
    fun clear() {
        updateInput { copy(
            status = RecruitStatus.Open,
            appIcon = null,
            appName = "",
            details = nonEmptyDetails(details),
            groupUrl = "",
            appUrl = "",
            webUrl = "",
        ) }
        updateUiState { copy(isSaving = false) }
    }

    /**－－－－－details　API　ここから！－－－－－**/
    //EmptyListを許さないための処理
    private fun nonEmptyDetails(list: List<DetailContent>): List<DetailContent> =
        list.ifEmpty { listOf(DetailContent.Text()) }

    //初期化
    fun initDetails() {
        updateInput { copy(details = nonEmptyDetails(details)) }
    }

    //募集内容にテキストフィールドを追加（ブランクのTextをdetailに追加することによりテキストフィールドがつかされる）
    fun addDetailText() {
        updateInput { copy(details = details + DetailContent.Text(text = "")) }
    }

    //募集内容に画像を追加
    fun addDetailImage(uri: Uri) {
        updateInput { copy(details = details + DetailContent.Image(uri= uri))}
    }

    //募集内容のテキスト部分を更新
    fun updateDetailText(id:String, text:String) {
        val updated = _uiState.value.input.details.map {   //既存のList<DetailContent>を1件ずつ変換して新しいListにする
            if (it.id == id && it is DetailContent.Text) it.copy(text = text) else it   //同じIDの場所だけTextを差し替え
        }
        updateInput { copy(details = updated) }
    }

    //IDを削除
    fun removeById(id:String) {
        val after = _uiState.value.input.details.filterNot{ it.id == id }  //同じidを持つものを除外
        updateInput { copy(details = nonEmptyDetails(after)) }
    }
    /**APIここまで**/
}