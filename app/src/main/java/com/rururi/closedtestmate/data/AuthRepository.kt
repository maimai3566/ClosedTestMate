package com.rururi.closedtestmate.data

import com.rururi.closedtestmate.data.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserFlow: Flow<UserSession?> //リアルタイムでログイン情報を監視
    fun currentUser(): UserSession?            //単発で取得したいとき
    //監視をvalとしているのはずっと同じインスタンスだから
    //単発がfunなのは、呼び出すたびに最新を取得しに行くから

    fun signOut()
}