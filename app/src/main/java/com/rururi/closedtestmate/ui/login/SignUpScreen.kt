package com.rururi.closedtestmate.ui.login

import android.R.attr.duration
import android.R.attr.onClick
import android.R.attr.password
import android.R.id.message
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.model.UserProfileUiState
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.rururi.closedtestmate.ui.anime.SlideMessage
import com.rururi.closedtestmate.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    onMessageReset: () -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onSignUpClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState,
    uiState: UserProfileUiState
) {
    val email = uiState.email
    val pw = uiState.pw
    val pw2 = uiState.pw2
    val error = uiState.error
    val success = uiState.success

    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { onMessageReset() }

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
            visualTransformation = if (!passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    val visibilityIcon =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        Spacer(modifier=Modifier.padding(dimensionResource(R.dimen.p_small)))
        OutlinedTextField(
            value = pw2,
            onValueChange = onConfirmPasswordChange,
            label = { Text(stringResource(R.string.pw2) + "*") },
            singleLine = true,
            visualTransformation = if (!passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    val visibilityIcon =
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            },
            keyboardActions = KeyboardActions( onDone = { keyboardController?.hide() }),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        //登録ボタン
        Button(
            onClick = onSignUpClick,
            modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)),
            enabled = email.isNotBlank() && pw.isNotBlank() && pw2.isNotBlank() && pw == pw2,
        ){
            Text(
                text = stringResource(R.string.signup_button),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier=Modifier.padding(dimensionResource(R.dimen.p_small)))
        //成功メッセージ
        if (success.isNotBlank()) {
            SlideMessage(message = success)
            LaunchedEffect(success) {
                delay(2000)
                navController.navigate(Screen.RecruitList.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
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

//画像UP用
@Composable
fun UploadImage(
    modifier: Modifier = Modifier,
    imageUri: Uri? = null,
    onImageSelected: (Uri) -> Unit = {},
){
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri!!)
    }

    Box(
        modifier = modifier
            .size(dimensionResource(R.dimen.img_size))
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary,shape = CircleShape)
            .clickable { launcher.launch("image/*") },
        contentAlignment = Alignment.Center,
    ){
        Log.d("ruruS", "imageUri: $imageUri")
        if (imageUri == null){
            Image(painter = painterResource(R.drawable.no_image),
                contentDescription = "no image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = imageUri,
                contentDescription = "アップロードした画像",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    SignupScreen(snackbarHostState = SnackbarHostState(), uiState = UserProfileUiState())
}
