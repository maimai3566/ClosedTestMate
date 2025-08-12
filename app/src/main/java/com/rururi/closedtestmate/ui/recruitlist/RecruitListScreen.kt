package com.rururi.closedtestmate.ui.recruitlist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.model.RecruitUiState
import com.rururi.closedtestmate.model.RecruitStatus
import androidx.compose.ui.res.stringResource
import com.rururi.closedtestmate.ui.components.AppIconImage
import com.rururi.closedtestmate.ui.components.formatDate

//テスター募集一覧
@Composable
fun RecruitListScreen(
    modifier: Modifier = Modifier,
    recruitList: List<RecruitUiState>,
    onClickRecruit: (String) -> Unit = {}
){
    //RecruitCardを一覧表示
    LazyColumn(
        modifier = modifier
    ) {
        items(recruitList) { uiState ->
            RecruitCard(
                uiState = uiState,
                onClick = { onClickRecruit(uiState.id) }
            )
        }
    }
}

//一覧に表示するカード
@Composable
fun RecruitCard(
    modifier: Modifier = Modifier,
    uiState: RecruitUiState,
    onClick: () -> Unit = {}
) {
    val appIcon = uiState.appIcon
    val appName = uiState.appName
    val status = uiState.status
    val postedAt = uiState.postedAt

    Card(
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.p_small)),
        modifier = modifier
            .padding(dimensionResource(R.dimen.p_small))
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Log.d("ruruS", "RecruitCard: $uiState")
        Row {
            AppIconImage(
                iconUri = appIcon,
                contentDescription = appName,
                size = R.dimen.icon_large,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.icon_large))
            )

            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
            Column{
                //募集ステータス
                Image(
                    painterResource(status.iconResId),
                    contentDescription = stringResource(status.labelResId),
                    modifier = Modifier.width(dimensionResource(R.dimen.icon_medium))
                )
                //アプリ名
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleMedium
                )
                //投稿日時（整形表示）
                val formattedDate = postedAt.formatDate()
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecruitListScreenPreview() {
    val recruitList = listOf(
        RecruitUiState(
            appName = "テストアプリ1",
            status = RecruitStatus.Open,
            postedAt = System.currentTimeMillis()
        ),
        RecruitUiState(
            appName = "テストアプリ2",
            status = RecruitStatus.Closed,
            postedAt = System.currentTimeMillis()
        ),
        RecruitUiState(
            appName = "テストアプリ3",
            status = RecruitStatus.Released,
            postedAt = System.currentTimeMillis()
        )
    )
    RecruitListScreen(recruitList = recruitList)
}

@Preview(showBackground = true)
@Composable
fun RecruitCardPreview() {
    val uiState = RecruitUiState(
        appName = "テストアプリ",
        status = RecruitStatus.Open,
        postedAt = System.currentTimeMillis()
    )
    RecruitCard(uiState = uiState)
}