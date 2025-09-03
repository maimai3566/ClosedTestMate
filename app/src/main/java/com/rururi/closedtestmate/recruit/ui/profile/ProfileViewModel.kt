package com.rururi.closedtestmate.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rururi.closedtestmate.auth.common.AuthError
import com.rururi.closedtestmate.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState(session = authRepository.currentUser()))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUserFlow.collect { user ->
                //リポジトリのカレントユーザを監視してセッションに突っ込む
                _uiState.update { it.copy(
                    session = user,
                    //現在の名前を名前変更のダイアログに表示
                    newName = if (!it.showEditDialog) user?.name.orEmpty() else it.newName
                ) }
            }
        }
    }

    //汎用更新
    fun updateUiState(update: ProfileUiState.() -> ProfileUiState) {
        _uiState.update(update)
    }

    /****　名前変更セクション　****/
    //編集ダイアログを開く
    fun startEdit() {
        val user = authRepository.currentUser()
        _uiState.update { it.copy(
            showEditDialog = true,      //ダイアログオープン
            newName = user?.name.orEmpty(), //現在の名前をセット
            error = null,
            success = false
        ) }
    }

    //編集ダイアログを閉じる
    fun dismissEdit() {
        _uiState.update { it.copy(showEditDialog = false, error = null,success = false) }
    }

    //名前変更
    fun onChangeName(newName: String) {
        val normalized = Normalizer.normalize(newName, Normalizer.Form.NFKC).trim()
        val limited = normalized.take(_uiState.value.maxLen)   //最初の24文字だけを許可
        _uiState.update { it.copy(newName = limited) }
    }

    //保存 uiStateのerrorはAuthErrorにしてobjectを増やしてもいいかもしれない？
    private var saveJob: Job? = null
    fun saveName() {
        //バリデーションチェック
        when {
            _uiState.value.newName.isBlank() -> {
                _uiState.update { it.copy(error = AuthError.NameEmpty) }
                return
            }
            _uiState.value.newName.length > _uiState.value.maxLen -> {
                _uiState.update { it.copy(error = AuthError.NameTooLong(_uiState.value.maxLen)) }
                return
            }
        }
        if (_uiState.value.isSaving || saveJob?.isActive == true) return     //連続タップ防止
        saveJob = viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, success = false) }
            val result = authRepository.updateName(_uiState.value.newName)
            if (result.isSuccess) {
                val fresh = authRepository.currentUser()    //AuthRepositoryから最新を取得
                _uiState.update { it.copy(isSaving = false, success = true, showEditDialog = false, error = null, newName = fresh?.name ?: "") }
            } else {
                val err = result.exceptionOrNull()
                _uiState.update { it.copy(isSaving = false, error = AuthError.fromThrowable(Throwable(err)), success = false) }
            }
        }
    }


    //自分の記事を見る
    //お気に入りを見る
    //ログアウト
    fun logout() = authRepository.signOut()
}

