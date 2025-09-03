package com.rururi.closedtestmate.auth.ui.forgot

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.core.ui.SlideMessage

@Composable
fun ForgotScreen(
    modifier: Modifier = Modifier,
    uiState: ForgotUiState = ForgotUiState(),
    onEmailChange: (String) -> Unit = {},
    onForgotPw: () -> Unit = {},
    onResetSuccess: () -> Unit = {},
    navToLogin: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.p_medium)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text =stringResource(R.string.forgot_pw_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)))
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(text = stringResource(R.string.email) + "*") },
            singleLine = true,
            keyboardActions = KeyboardActions( onDone = { keyboardController?.hide() }),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)))

        Button(
            onClick = onForgotPw,
            modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)),
            enabled = uiState.isValid,
        ) {
            Text(
                text = stringResource(R.string.forgot_pw_button),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        //パスワードリセット成功
        if (uiState.success) {
            //メッセージ表示中は戻るボタンを無効
            BackHandler(enabled = true) {}
            SlideMessage(
                message = stringResource(R.string.forgot_pw_success),
                onAnimationEnd = {
                    onResetSuccess()
                    navToLogin()
                }
            )
        }
        //エラー表示
        uiState.error?.let{
            Text(
                text = stringResource(it.resId),
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotScreen()
}