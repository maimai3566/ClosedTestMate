package com.rururi.closedtestmate.ui.recruitnew

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.app.navigation.Screen
import com.rururi.closedtestmate.core.ui.AppIconImage
import com.rururi.closedtestmate.core.ui.Edita
import com.rururi.closedtestmate.core.ui.SlideMessage
import com.rururi.closedtestmate.recruit.domain.RecruitStatus
import com.rururi.closedtestmate.recruit.ui.recruitnew.RecruitNewEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun RecruitNewScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    uiState: RecruitNewUiState,
    eventFlow: SharedFlow<RecruitNewEvent?>,
    onStatusChange: (RecruitStatus) -> Unit = {},
    onAppIconChange: (Uri) -> Unit = {},
    onAppNameChange: (String) -> Unit = {},
    onGroupUrlChange: (String) -> Unit = {},
    onAppUrlChange: (String) -> Unit = {},
    onWebUrlChange: (String) -> Unit = {},
    onDetailTextChange: (String, String) -> Unit = { _, _ -> },
    onDetailTextAdd: () -> Unit = {},
    onDetailImageAdd: () -> Unit = {},
    onDetailRemove: (String) -> Unit = {},
    onSaveClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
//    onMsgEnd: () -> Unit = {},
) {
    val context = LocalContext.current
    val focus = remember { FocusRequester() }   //フォーカスが当たっているコンポーネント
    val focusManager = LocalFocusManager.current

    var isStatusMenuExpanded by remember { mutableStateOf(false) }  //ドロップダウンの表示状態
    var showSlideMessage by remember { mutableStateOf(false) }  //スライドメッセージ監視用
    var slideMsgText by remember { mutableStateOf("") }     //スライドメッセージに表示させる文字
    var saveF by remember { mutableStateOf(false) }     //保存が成功ならtrue

    LaunchedEffect(Unit) {
        eventFlow.collect {e ->
            when (e) {
                is RecruitNewEvent.SaveSucceeded -> {
                    slideMsgText = context.getString(R.string.save_succeeded)
                    showSlideMessage = true
                    saveF = true
                }
                is RecruitNewEvent.SaveFailed -> {
                    slideMsgText = context.getString(R.string.save_failed)
                    showSlideMessage = true
                    saveF = false
                }
                else -> {
                    showSlideMessage = false
                    saveF = false
                }
            }
        }
    }

    Box(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //募集ステータス
            item {
                Box {
                    Image(
                        painter = painterResource(id = uiState.input.status.iconResId),
                        contentDescription = stringResource(id = uiState.input.status.labelResId),
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
                                onClick = {
                                    onStatusChange(status)
                                    isStatusMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            //アプリアイコン＋アプリ名
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //アプリアイコン
                    AppIconImage(
                        iconUri = uiState.input.appIcon,
                        contentDescription = stringResource(R.string.lbl_recruit_app_icon),
                        size = R.dimen.icon_large,
                        onImageSelected = onAppIconChange
                    )

                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.p_medium)))
                    //アプリ名
                    OutlinedTextField(
                        value = uiState.input.appName,
                        onValueChange = onAppNameChange,
                        label = { Text(stringResource(R.string.lbl_recruit_app_name) + "*") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        isError = uiState.input.appName.isBlank(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            /**－－－－－－－募集内容－－－－－－－**/
            item {
                Edita(
                    items = uiState.input.details,
                    editaTitle = stringResource(R.string.lbl_recruit_details) + "*",
                    label = stringResource(R.string.lbl_recruit_detail),
                    onValueChange = onDetailTextChange ,   //(id,value) -> Unit
                    onAddText = onDetailTextAdd,      //Textフィールドを追加
                    onAddImage = onDetailImageAdd,   //イメージを追加
                    onRemove = onDetailRemove,        //選択したものを削除
                )
            }

            //GroupURL
            item {
                OutlinedTextField(
                    value = uiState.input.groupUrl,
                    onValueChange = onGroupUrlChange,
                    label = { Text(stringResource(R.string.lbl_recruit_group_url) + "*") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Uri
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = uiState.input.groupUrl.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focus)
                )
            }

            //AppURL
            item {
                OutlinedTextField(
                    value = uiState.input.appUrl,
                    onValueChange = onAppUrlChange,
                    label = { Text(stringResource(R.string.lbl_recruit_app_url) + "*") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Uri
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = uiState.input.appUrl.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            //webURL
            item {
                OutlinedTextField(
                    value = uiState.input.webUrl,
                    onValueChange = onWebUrlChange,
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
            }

            //button
            item {
                Row(
                    modifier = Modifier.imePadding()
                ) {
                    OutlinedButton(
                        onClick = onClearClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.button_recruit_clear))
                    }
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.p_medium)))
                    //新規投稿
                    Button(
                        onClick = onSaveClick,
                        enabled = uiState.input.isValid,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.button_recruit_add))
                    }
                }
            }
        }

        if (showSlideMessage) {
            SlideMessage(
                message = slideMsgText,
                onAnimationEnd = {
                    if (saveF) {
//                        onMsgEnd() //アニメが終わったらisSavingをfalseに戻す
                        navController.navigate(Screen.RecruitList.route) //アニメが終わってから画面遷移
                        onClearClick() //入力値クリア
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecruitNewScreenPreview() {
    val navController = rememberNavController()
    val uiState = RecruitNewUiState()
    RecruitNewScreen(navController = navController, uiState = uiState, eventFlow = MutableSharedFlow())
}
