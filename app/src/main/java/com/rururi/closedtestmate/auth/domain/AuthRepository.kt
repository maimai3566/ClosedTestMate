package com.rururi.closedtestmate.auth.domain

import com.rururi.closedtestmate.auth.domain.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserFlow: Flow<UserSession?> //リアルタイムでログイン情報を監視
    fun currentUser(): UserSession?            //単発で取得したいとき
    //監視をvalとしているのはずっと同じインスタンスだから
    //単発がfunなのは、呼び出すたびに最新を取得しに行くから

    //名前変更(FirebaseのupdateProfileは非同期なのでsuspend)
    suspend fun updateName(name: String): Result<Unit>

    fun signOut()
}