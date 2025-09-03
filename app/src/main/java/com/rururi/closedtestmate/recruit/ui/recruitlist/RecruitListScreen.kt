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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.recruit.domain.RecruitStatus
import com.rururi.closedtestmate.core.ui.AppIconImage
import com.rururi.closedtestmate.core.ui.ErrorScreen
import com.rururi.closedtestmate.core.ui.LoadingScreen
import com.rururi.closedtestmate.core.util.formatDate

//テスター募集一覧
@Composable
fun RecruitListScreen(
    modifier: Modifier = Modifier,
    uiState: RecruitListUiState,
    onClickRecruit: (String) -> Unit = {}
){
    when {
        uiState.isLoading -> {
            LoadingScreen()
        }
        uiState.error != null -> {
            ErrorScreen(error = uiState.error)
        }
        else -> {
            //RecruitCardを一覧表示
            LazyColumn(
                modifier = modifier
            ) {
                items(uiState.recruitList) { item ->
                    RecruitCard(
                        item = item,
                        onClick = { onClickRecruit(item.id) }
                    )
                }
            }
        }
    }
}

//一覧に表示するカード
@Composable
fun RecruitCard(
    modifier: Modifier = Modifier,
    item: RecruitListItem,
    onClick: () -> Unit = {}
) {
    Card(
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.p_small)),
        modifier = modifier
            .padding(dimensionResource(R.dimen.p_small))
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Log.d("ruruS", "RecruitCard: $item")
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.p_small))) {
            Row {
                AppIconImage(
                    iconUri = item.authorIcon,
                    contentDescription = item.authorName,
                    size = R.dimen.icon_extra_small,
                )
                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
                Text(
                    text = item.authorName,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_extra_small)))
            Row {
                AppIconImage(
                    iconUri = item.appIcon,
                    contentDescription = item.appName,
                    size = R.dimen.icon_large,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.icon_large))
                )

                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        //募集ステータス
                        Image(
                            painterResource(item.status.iconResId),
                            contentDescription = stringResource(item.status.labelResId),
                            modifier = Modifier.width(dimensionResource(R.dimen.icon_medium))
                        )
                        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
                        //投稿日時（整形表示）
                        val formattedDate = item.postedAt.formatDate()
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    //アプリ名
                    Text(
                        text = item.appName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecruitListScreenPreview() {
    val recruitList = listOf(
        RecruitListItem(
            id = "1",
            appName = "テストアプリ1",
            appIcon = null,
            status = RecruitStatus.Open,
            postedAt = System.currentTimeMillis(),
            authorName = "テストさん",
            authorIcon = null
        ),
        RecruitListItem(
            id = "2",
            appName = "テストアプリ2",
            appIcon = null,
            status = RecruitStatus.Closed,
            postedAt = System.currentTimeMillis(),
            authorName = "テストさん",
            authorIcon = null
        ),
        RecruitListItem(
            id = "3",
            appName = "テストアプリ3",
            appIcon = null,
            status = RecruitStatus.Released,
            postedAt = System.currentTimeMillis(),
            authorName = "テストさん",
            authorIcon = null
        ),
    )
    RecruitListScreen(uiState = RecruitListUiState(recruitList = recruitList))
}

@Preview(showBackground = true)
@Composable
fun RecruitCardPreview() {
    val item = RecruitListItem(
        id = "1",
        appName = "テストアプリ1",
        appIcon = null,
        status = RecruitStatus.Open,
        postedAt = System.currentTimeMillis(),
        authorName = "テストさん",
        authorIcon = null
    )
    RecruitCard(item = item)
}