package com.rururi.closedtestmate.core.util

import android.net.Uri
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
import com.rururi.closedtestmate.auth.common.AuthError
import com.rururi.closedtestmate.recruit.domain.DetailContent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//Long型の日付変換
fun Long.formatDate():String {
    val date = Date(this)   //Long型の日付をDate型に変換
    val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())    //フォーマット指定
    return format.format(date)  //output
}
//空白やNullを除外してUriに変換
fun String?.toUriOrNull(): Uri? =
    this?.takeIf { it.isNotBlank() && it != "null" }?.toUri()

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

//DetailContentの該当するIDの内容を差し替え
inline fun <reified T: DetailContent> List<DetailContent>.replaceById(
    id: String,
    transform: DetailContent.() -> DetailContent
): List<DetailContent> = map { current ->
    if(current.id == id) transform(current) else current
}

