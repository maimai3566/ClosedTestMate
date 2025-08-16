package com.rururi.closedtestmate.ui.components

import android.R.attr.description
import android.R.attr.text
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.DocumentSnapshot
import com.rururi.closedtestmate.auth.common.AuthError
import com.rururi.closedtestmate.model.DetailContent
import com.rururi.closedtestmate.model.RecruitStatus
import com.rururi.closedtestmate.model.RecruitUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

//Long型の日付変換
fun Long.formatDate():String {
    val date = Date(this)   //Long型の日付をDate型に変換
    val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())    //フォーマット指定
    return format.format(date)  //output
}

//DetailContentをMap（Firestore対応）に変換する拡張関数
fun DetailContent.toMap(): Map<String, String> {
    return when (this) {
        is DetailContent.Text -> mapOf("type" to "text", "id" to id, "text" to text)
        is DetailContent.Image -> mapOf("type" to "image", "id" to id, "uri" to uri.toString())
    }
}

//MapをDetailContentに変換する拡張関数
fun Map<*,*>.toDetailContent(): DetailContent? {
    val type = this["type"] as? String ?: return null
    val id = this["id"] as? String ?: UUID.randomUUID().toString()
    return when (type) {
        "text" -> {
            val text = this["text"] as? String ?: return null
            DetailContent.Text(id,text)
        }
        "image" -> {
            val uri = this["uri"] as? String ?: return null
            DetailContent.Image(id,uri.toUri())
        }
        else -> null
    }
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
            details = (get("details") as? List<*>)  //Firestoreのdetailsは配列型だけど中身はわからないのでList<*>
                ?.mapNotNull { (it as? Map<*, *>)?.toDetailContent() }  //Map<*,*>型にキャストできるか確認してからDetailContentに変換
                ?: emptyList(), //変換に失敗したら空のリストにする
            groupUrl = getString("groupUrl") ?:"",
            appUrl = getString("appUrl") ?:"",
            webUrl = getString("webUrl") ?:"",
            postedAt = getLong("postedAt") ?: 0L,
            authorId = getString("authorId") ?:"",
            authorName = getString("authorName") ?:"",
            authorIcon = getString("authorIcon")
                ?.takeIf { it.isNotBlank() && it !="null"}  //ブランクでもnullでもなければ文字列をURIに変換、それ以外はnull
                ?.toUri()
        )
    } catch (e: Exception) {
        Log.e("Extension", "RecruitUiStateに変換できないエラー：", e)
        null
    }
}

//ステータスをFirestoreの文字列に変換する拡張関数
fun RecruitUiState.toMap(): Map<String, Any> {
    return mapOf(
        "appName" to appName,
        "appIcon" to appIcon.toString(),
        "status" to status.toFirestoreString(),
        "details" to details.map { it.toMap() },
        "groupUrl" to groupUrl,
        "appUrl" to appUrl,
        "webUrl" to webUrl,
        "postedAt" to postedAt,
        "authorId" to authorId,
        "authorName" to authorName,
        "authorIcon" to authorIcon.toString()
    )
}

//ステータスを文字列に変換する関数
fun RecruitStatus.toFirestoreString(): String = when (this) {
    RecruitStatus.Open -> "募集中"
    RecruitStatus.Closed -> "募集終了"
    RecruitStatus.Released -> "リリース済"
}

//URLの文字列をリンクに変換
fun String.toAnnotatedString(url:String,color: Color = Color.Blue):AnnotatedString {
    return buildAnnotatedString {
        pushStringAnnotation(tag = "URL", annotation = url)
        withStyle(style = SpanStyle(color = color, textDecoration = TextDecoration.Underline)) {
            append(this@toAnnotatedString)
        }
        pop()
    }
}

//例外→AuthErrorの共通変換
fun Throwable.toAuthError(): AuthError {
    val code = when (this) {
        is FirebaseAuthInvalidUserException -> this.errorCode
        is FirebaseAuthInvalidCredentialsException -> this.errorCode
        is FirebaseAuthUserCollisionException -> this.errorCode
        is FirebaseAuthException -> this.errorCode
        else -> null
    }
    return AuthError.fromException(code)
}

