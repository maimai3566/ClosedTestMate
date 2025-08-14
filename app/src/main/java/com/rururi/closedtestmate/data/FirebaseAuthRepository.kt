package com.rururi.closedtestmate.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

//AuthRepositoryの実装
class FirebaseAuthRepository @Inject constructor():AuthRepository{
    //今の瞬間の情報を取得したいとき
    override fun currentUser(): UserSession? {
        //user＝現在ログイン中のユーザ（いなければnullを返して終わり）
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        return UserSession(     //Firebaseから取得したユーザ情報をUserSessionに詰め直して返す
            uid = user.uid,
            name = user.displayName,
            photoUrl = user.photoUrl,
        )
    }
    //ログイン状態の変化を監視
    override val currentUserFlow: Flow<UserSession?> =
        callbackFlow {      //コールバック型のAPI→Flowに橋渡しするための器
            val listener = FirebaseAuth.AuthStateListener { auth -> //ログイン・ログアウト・ユーザ切り替えで呼ばれる
                val user = auth.currentUser
                trySend(    //非ブロッキングに値を流す
                    user?.let{
                        UserSession(uid = it.uid, name = it.displayName, photoUrl = it.photoUrl)
                    }
                )
            }
            FirebaseAuth.getInstance().addAuthStateListener(listener)
            awaitClose {    //購読がキャンセルされた時の後処理
                FirebaseAuth.getInstance().removeAuthStateListener(listener)    //リスナーを外す
            }
        }
}
