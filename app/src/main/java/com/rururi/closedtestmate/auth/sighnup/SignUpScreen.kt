package com.rururi.closedtestmate.auth.sighnup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.model.UserProfileUiState
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rururi.closedtestmate.ui.anime.SlideMessage
import com.rururi.closedtestmate.ui.navigation.Screen

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    uiState: SignupUiState = SignupUiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onSignUpClick: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var pwVisible by remember { mutableStateOf(false) }
    var pw2Visible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = stringResource(R.string.signup_title),
            style = MaterialTheme.typography.headlineMedium
        )
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email) + "*") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            )
        )
        Spacer(modifier=Modifier.padding(dimensionResource(R.dimen.p_small)))
        OutlinedTextField(
            value = uiState.pw,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.pw) + "*") },
            singleLine = true,
            visualTransformation = if (!pwVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { pwVisible = !pwVisible }) {
                    val visibilityIcon =
                        if (pwVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (pwVisible) "Hide password" else "Show password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            )
        )
        Spacer(modifier=Modifier.padding(dimensionResource(R.dimen.p_small)))
        OutlinedTextField(
            value = uiState.pw2,
            onValueChange = onConfirmPasswordChange,
            label = { Text(stringResource(R.string.pw2) + "*") },
            singleLine = true,
            visualTransformation = if (!pw2Visible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { pw2Visible = !pw2Visible }) {
                    val visibilityIcon =
                        if (pw2Visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (pw2Visible) "Hide password" else "Show password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.moveFocus(FocusDirection.Exit) //フォーカスをどこにもあてないようにして
                    keyboardController?.hide()  //キーボードを閉じる
                }
            ),
        )

        //登録ボタン
        Button(
            onClick = onSignUpClick,
            modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)),
            enabled = uiState.isValid,
        ){
            Text(
                text = stringResource(R.string.signup_button),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier=Modifier.padding(dimensionResource(R.dimen.p_small)))

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
fun SignupScreenPreview() {
    SignupScreen()
}
