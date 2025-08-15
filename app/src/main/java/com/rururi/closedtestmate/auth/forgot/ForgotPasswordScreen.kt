package com.rururi.closedtestmate.auth.forgot

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.model.UserProfileUiState
import com.rururi.closedtestmate.ui.anime.SlideMessage
import com.rururi.closedtestmate.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    onMessageReset: () -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onForgotPw: () -> Unit = {},
    uiState: UserProfileUiState,
) {
    val email = uiState.email
    val error = uiState.error
    val success = uiState.success

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
            value = email,
            onValueChange = onEmailChange,
            label = { Text(text = stringResource(R.string.email) + "*") },
            singleLine = true,
            keyboardActions = KeyboardActions( onDone = { keyboardController?.hide() }),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)))

        Button(
            onClick = onForgotPw,
            modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)),
            enabled = email.isNotBlank(),
        ) {
            Text(
                text = stringResource(R.string.forgot_pw_button),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        //成功メッセージ
        if (success.isNotBlank()) {
            SlideMessage(message = success)
            LaunchedEffect(success) {
                delay(2000)
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
        //失敗したときの処理
        if(error.isNotBlank()) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(uiState = UserProfileUiState())
}