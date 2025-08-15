package com.rururi.closedtestmate.auth.login

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
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.ui.navigation.Screen

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    uiState: LoginUiState = LoginUiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLogin: () -> Unit = {},
    onForgotPw: () -> Unit = {},
    onSignUp: () -> Unit = {},
    onSkipLogin: () -> Unit = {},
){
    val keyboardController = LocalSoftwareKeyboardController.current    //キーボード
    var pwVisible by remember { mutableStateOf(false) } //パスワード表示有無

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
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(text = stringResource(R.string.email) + "*") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
        //PW
        OutlinedTextField(
            value = uiState.pw,
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
            enabled = uiState.isValid
        ) {
            Text(text = stringResource(R.string.login))
        }
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
        //エラー表示
        uiState.error?.let{
            Text(
                text = stringResource(it.resId),
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
                onClick = onSignUp,
            ){
                Text(
                    stringResource(R.string.signup),
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)))
        //ログインしないで続ける
        OutlinedButton(
            onClick = onSkipLogin,
        ) {
            Text(text = stringResource(R.string.skip_login))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    LoginScreen()
}