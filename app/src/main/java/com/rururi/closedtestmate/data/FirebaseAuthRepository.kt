package com.rururi.closedtestmate.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

//AuthRepositoryの実装
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
):AuthRepository{
//    val auth = FirebaseAuth.getInstance()
    //FirebaseをUserSessionに変換する関数
    private fun FirebaseUser.toSession(): UserSession? {
        return UserSession(
            uid = uid,
            email = email,
            name = displayName,
            photoUrl = photoUrl
        )
    }

    //stateFlow
    private val _session = MutableStateFlow<UserSession?>(auth.currentUser?.toSession())
    override val currentUserFlow: Flow<UserSession?> = _session.asStateFlow()

    init {
        //アプリは起動したらログイン状態の変化を監視する
        auth.addAuthStateListener { a ->
            _session.value = a.currentUser?.toSession()
        }
    }

    //今の情報を取得したいとき
    override fun currentUser(): UserSession? = _session.value

    //名前変更の処理
    override suspend fun updateName(name: String): Result<Unit> {
        val user = auth.currentUser
            ?: return Result.failure(IllegalStateException("Not logged in"))    //userがいなかったら結果にfailを返す
        val newName = name.trim()   //ブランク防止
        if (newName.isEmpty()) return Result.failure(IllegalArgumentException("Name cannot be empty"))
        if (newName.length > 24) return Result.failure(IllegalArgumentException("Name is too long"))
        if (newName == user.displayName) return Result.success(Unit)    //変更なし

        return runCatching {    //更新結果を返す
            val req = userProfileChangeRequest { displayName = newName }    //名前変更のリクエストを作成
            user.updateProfile(req).await()     //名前を変更するようリクエストする
            //プロファイル更新はAuthStateListenerが必ずしも発火しないので、明示的に最新化する
            user.reload().await()   //サーバーをリロードして、最新を再取得
            _session.value = auth.currentUser?.toSession()  //Flowへ反映
            Unit                    //cunCachingの戻り値は最後の式なので、Unit（結果）を返してもらう
        }
    }

    //ログアウトの処理
    override fun signOut() {
        auth.signOut()
    }
}