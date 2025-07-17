package com.rururi.closedtestmate.ui.login

import android.R.attr.onClick
import android.R.attr.password
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.model.UserProfileUiState

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onSignUpClick: () -> Unit = {},
    uiState: UserProfileUiState
) {
    val email = uiState.email
    val pw = uiState.pw
    val error = uiState.error

    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = stringResource(R.string.signup_title),
            style = MaterialTheme.typography.displaySmall
        )
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.email) + "*") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier=Modifier.padding(dimensionResource(R.dimen.p_small)))
        OutlinedTextField(
            value = pw,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.pw) + "*") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    val visibilityIcon =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )
        Spacer(modifier=Modifier.padding(dimensionResource(R.dimen.p_medium)))
        //ニックネーム
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(stringResource(R.string.display_name)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        Spacer(modifier=Modifier.padding(dimensionResource(R.dimen.p_small)))
        //アバター
        UploadImage()
        Spacer(modifier=Modifier.padding(dimensionResource(R.dimen.p_medium)))
        //登録ボタン
        Button(
            onClick = onSignUpClick,
            modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)),
            enabled = email.isNotBlank() && pw.isNotBlank()
        ){
            Text(
                text = stringResource(R.string.signup_button),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier=Modifier.padding(dimensionResource(R.dimen.p_small)))
        //エラーメッセージ
        if (error.isNotBlank()){
            Text(
                text = error.toString(),
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier=Modifier.padding(dimensionResource(R.dimen.p_small)))
        }
    }
}

//画像UP用のコンポーネント
@Composable
fun UploadImage(){
    val context = LocalContext.current
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri.value = uri
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        selectedImageUri?.let {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    SignupScreen(onEmailChange = {}, onPasswordChange = {}, onSignUpClick = {}, uiState = UserProfileUiState())
}
