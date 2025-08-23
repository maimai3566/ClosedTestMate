package com.rururi.closedtestmate.ui.profile

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.auth.common.AuthError
import com.rururi.closedtestmate.data.UserSession
import com.rururi.closedtestmate.ui.components.AppIconImage
import com.rururi.closedtestmate.ui.components.IconShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileUiState,
    onImageSelected: ((Uri) -> Unit)? = null,
    onShowEdit: () -> Unit = {},
    onMyRecruitClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onNameChange: (String) -> Unit = {},
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    val name = uiState.session?.name ?: "No Name"
    val email = uiState.session?.email ?: "No Email"
    val photoUrl = uiState.session?.photoUrl

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.p_medium)),
    ){
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            //ユーザアイコン
            AppIconImage(
                iconUri = photoUrl,
                contentDescription = stringResource(R.string.profile_icon),
                size = R.dimen.icon_large,
                onImageSelected = onImageSelected,
                shape = IconShape.Circle,
                border = dimensionResource(R.dimen.p_extra_small),
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.p_medium)))
            Column {
                //ユーザ名
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = name, style = MaterialTheme.typography.bodyLarge)
                    IconButton(
                        onClick = onShowEdit,
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = stringResource(R.string.profile_name))
                    }
                }
                //メールアドレス
                Text(text = email, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_large)))
        //ショートカット
        Button(
            onClick = onMyRecruitClick,
            modifier = Modifier.width(dimensionResource(R.dimen.button_large)),
        ) {
            Text(
                text = stringResource(R.string.profile_my_recruit),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_medium)))
        Button(
            onClick = onFavoriteClick,
            modifier = Modifier.width(dimensionResource(R.dimen.button_large)),
        ) {
            Text(
                text = stringResource(R.string.profile_favorite),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_large)))
        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.width(dimensionResource(R.dimen.button_large)),
        ) {
            Text(
                text = stringResource(R.string.profile_logout),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    //名前変更ボックス
    if (uiState.showEditDialog) {
        EditNameDialog(
            uiState = uiState,
            onNameChange = onNameChange,
            onDismiss = onDismiss,
            onConfirm = onConfirm
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditNameDialog(
    uiState: ProfileUiState,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {

    AlertDialog(
        onDismissRequest = { if (!uiState.isSaving) onDismiss() },  //保存中は閉じない
        title = { Text(text = stringResource(R.string.profile_name_change)) },
        text = {
            Column {
                //入力BOX
                OutlinedTextField(
                    value = uiState.newName,
                    onValueChange = onNameChange,
                    singleLine = true,
                    label = { Text(text = stringResource(R.string.profile_name)) },
                    supportingText = { Text(text = "${uiState.newName.length} / ${uiState.maxLen}") },
                    enabled = !uiState.isSaving,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { if (uiState.isValid) onConfirm() })
                )
                //エラー表示
                uiState.error?.let{ err ->  //エラーがあれば
                    Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
                    val msg = when (err) {
                        is AuthError.NameTooLong -> stringResource(err.resId, err.maxLen)
                        else -> stringResource(err.resId)
                    }
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = uiState.isValid,
                modifier = Modifier.width(dimensionResource(R.dimen.button_small))
            ) {
                Text(text = stringResource(R.string.button_save))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.width(dimensionResource(R.dimen.button_small))) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditNameDialogPreview() {
    EditNameDialog(onDismiss = {}, onConfirm = {}, onNameChange = {}, uiState = ProfileUiState())
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val uiState = ProfileUiState(
        session = UserSession(
            name = "No Name",
            email = "No Email",
            photoUrl = null,
            uid = "No Uid"
        ),
        showEditDialog = false,
        newName = "",
        maxLen = 24,
        isSaving = false,
        success = false,
        error = null
    )
    ProfileScreen(uiState = uiState)
}

