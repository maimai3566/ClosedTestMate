package com.rururi.closedtestmate.ui.login

import android.R.attr.enabled
import android.R.attr.text
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.model.UserProfileUiState

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onMessageReset: () -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLogin: () -> Unit = {},
    onForgotPw: () -> Unit = {},
    onSignup: () -> Unit = {},
    onSkipLogin: () -> Unit = {},
    uiState: UserProfileUiState,
){
    val email = uiState.email
    val password = uiState.pw
    val isLoggedIn = uiState.isLoggedIn

    val keyboardController = LocalSoftwareKeyboardController.current    //キーボード

    var pwVisible by remember { mutableStateOf(false) } //パスワード表示有無

    LaunchedEffect(Unit) { onMessageReset() }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text =stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)))
        //email
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(text = stringResource(R.string.email) + "*") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
        //PW
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(text = stringResource(R.string.pw) + "*") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (pwVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (pwVisible) stringResource(R.string.pw_hide) else stringResource(R.string.show_pw)
                IconButton(onClick = { pwVisible = !pwVisible }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            }
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)))
        //ログインボタン
        Button(
            onClick = {
                keyboardController?.hide()  //キーボードを閉じる
                onLogin()     //ログイン処理
            },
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            Text(text = stringResource(R.string.login))
        }
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
        //エラー表示
        if (uiState.error.isNotBlank()) {
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
        }
        //パスワードを忘れた / 新規登録
        Row{
            //パスワードを忘れた
            TextButton(
                onClick = onForgotPw,
            ){
                Text(
                    stringResource(R.string.forgot_pw),
                    textDecoration = TextDecoration.Underline
                )
            }
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)))
            //サインアップ
            TextButton(
                onClick = onSignup,
            ){
                Text(
                    stringResource(R.string.signup),
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)))
        //ログインしていないときだけ表示、押すと募集一覧へ遷移
        if (!isLoggedIn) {
            OutlinedButton(
                onClick = onSkipLogin,
            ) {
                Text(text = stringResource(R.string.skip_login))
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val uiState = UserProfileUiState()
    LoginScreen(uiState = uiState)
}