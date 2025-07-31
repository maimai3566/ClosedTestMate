package com.rururi.closedtestmate.ui.components

import android.R.attr.description
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.DocumentSnapshot
import com.rururi.closedtestmate.model.RecruitStatus
import com.rururi.closedtestmate.model.RecruitUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//Long型の日付変換
fun Long.formatDate():String {
    val date = Date(this)   //Long型の日付をDate型に変換
    val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())    //フォーマット指定
    return format.format(date)  //output
}

//Firestoreから読み込んだデータをRecruitUiStateに変換する拡張関数
fun DocumentSnapshot.toRecruitUiState(): RecruitUiState? {
    return try {
        RecruitUiState(
            id = id,
            appName = getString("appName") ?: "",
            appIcon = getString("appIcon")
                ?.takeIf { it.isNotBlank() && it !="null"}   //""でも"null"でもなければ文字列をURIに変換、それ以外はNull
                ?.toUri(),
            status = getString("status")?.let { RecruitStatus.fromFirestoreString(it) } ?: RecruitStatus.Open,
            description = getString("description") ?: "",
            groupUrl = getString("groupUrl") ?:"",
            appUrl = getString("appUrl") ?:"",
            webUrl = getString("webUrl") ?:"",
            postedAt = getLong("postedAt") ?: 0L,
        )
    } catch (e: Exception) {
        Log.e("Extension", "RecruitUiStateに変換できないエラー：", e)
        null
    }
}