package com.rururi.closedtestmate.ui.recruitnew

import android.R.attr.label
import android.R.attr.singleLine
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.model.DetailContent
import com.rururi.closedtestmate.model.RecruitStatus
import com.rururi.closedtestmate.model.RecruitUiState
import com.rururi.closedtestmate.model.SaveStatus
import com.rururi.closedtestmate.ui.anime.SlideMessage
import com.rururi.closedtestmate.ui.components.AppIconImage
import com.rururi.closedtestmate.ui.components.Edita
import com.rururi.closedtestmate.ui.components.EditaLayout
import com.rururi.closedtestmate.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun RecruitNewScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    viewModel: RecruitNewViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focus = remember { FocusRequester() }   //フォーカスが当たっているコンポーネント
    val focusManager = LocalFocusManager.current

    var isStatusMenuExpanded by remember { mutableStateOf(false) }  //ドロップダウンの表示状態

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //募集ステータス選択
            Box {
                Image(
                    painter = painterResource(id = uiState.status.iconResId),
                    contentDescription = stringResource(id = uiState.status.labelResId),
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.icon_extra_large))
                        .clickable { isStatusMenuExpanded = true }
                )
                DropdownMenu(
                    expanded = isStatusMenuExpanded,
                    onDismissRequest = { isStatusMenuExpanded = false })
                {
                    val recruitStatusList =
                        listOf(RecruitStatus.Open, RecruitStatus.Closed, RecruitStatus.Released)
                    recruitStatusList.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(stringResource(id = status.labelResId)) },
                            onClick = { viewModel.updateUiState { copy(status = status) }
                                isStatusMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                //アプリアイコン
                AppIconImage(
                    iconUri = uiState.appIcon,
                    contentDescription = stringResource(R.string.lbl_recruit_app_icon),
                    size = R.dimen.icon_large,
                    onImageSelected = { viewModel.updateUiState { copy(appIcon = it) } }
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.p_medium)))
                //アプリ名
                OutlinedTextField(
                    value = uiState.appName,
                    onValueChange = { viewModel.updateUiState { copy(appName = it) } },
                    label = { Text(stringResource(R.string.lbl_recruit_app_name) + "*") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    isError = uiState.appName.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            /**－－－－－－－募集内容－－－－－－－**/
            //画像選択
            val pickImage = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                if(uri != null) viewModel.addDetailImage(uri)   //detailsの最後に画像を追加
            }
            Edita(
                items = uiState.details,
                editaTitle = stringResource(R.string.lbl_recruit_details),
                onValueChange = viewModel::updateDetailText ,   //(id,value) -> Unit
                onAddText = { viewModel.addDetailText() },      //Textフィールドを追加
                onAddImage = { pickImage.launch("image/*") },   //イメージを追加
                onRemove = { viewModel.removeById(it) },        //選択したものを削除
                layout = EditaLayout.BottomBar,
            )

            //GroupURL
            OutlinedTextField(
                value = uiState.groupUrl,
                onValueChange = { viewModel.updateUiState { copy(groupUrl = it) } },
                label = { Text(stringResource(R.string.lbl_recruit_group_url) + "*") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Uri
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = uiState.groupUrl.isBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focus)
            )
            //AppURL
            OutlinedTextField(
                value = uiState.appUrl,
                onValueChange = { viewModel.updateUiState { copy(appUrl = it) } },
                label = { Text(stringResource(R.string.lbl_recruit_app_url) + "*") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Uri
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                isError = uiState.appUrl.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.webUrl,
                onValueChange = { viewModel.updateUiState { copy(webUrl = it) } },
                label = { Text(stringResource(R.string.lbl_recruit_web_url)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Uri
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row {
                OutlinedButton(
                    onClick = { viewModel.clear() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.button_recruit_clear))
                }
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.p_medium)))
                //新規投稿
                Button(
                    onClick = { viewModel.saveRecruit() },
                    enabled = uiState.isValid,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.button_recruit_add))
                }
            }

        }
        if (uiState.isSaved) {
            SlideMessage(
                message = stringResource(R.string.msg_recruit_saved),
                onAnimationEnd = {
                    viewModel.onMsgEnd() //アニメが終わったらisSavedをfalseに戻す
                    navController.navigate(Screen.RecruitList.route) //アニメが終わってから画面遷移
                    viewModel.clear() //入力値クリア
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecruitNewScreenPreview() {
    RecruitNewScreen()
}
