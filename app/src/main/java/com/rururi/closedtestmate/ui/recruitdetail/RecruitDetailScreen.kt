package com.rururi.closedtestmate.ui.recruitdetail

import android.R.attr.data
import android.R.attr.text
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rururi.closedtestmate.model.RecruitUiState
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.model.LoadState
import com.rururi.closedtestmate.model.RecruitStatus
import com.rururi.closedtestmate.ui.components.AppIconImage
import com.rururi.closedtestmate.ui.components.TitleUrl
import com.rururi.closedtestmate.ui.components.formatDate

@Composable
fun RecruitDetailScreen(
    modifier: Modifier = Modifier,
    recruitId: String,   //nullやブランクはだめ
    uiState: LoadState
){
    Log.d("RecruitDetailScreen", "recruitId: $recruitId, state: ${uiState::class.simpleName}")
    when(uiState) {
        is LoadState.Loading -> {
            Text(
                text = "読み込み中・・・",
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier.padding(dimensionResource(R.dimen.p_small))
            )
        }
        is LoadState.Error -> {
            Text(
                text = "エラーが発生しました",
                style = MaterialTheme.typography.titleMedium,
                modifier = modifier.padding(dimensionResource(R.dimen.p_small))
            )
        }
        is LoadState.Success -> {
            SuccessScreen(uiState = uiState.recruit)
        }
    }
}

@Composable
fun SuccessScreen(
    modifier: Modifier = Modifier,
    uiState: RecruitUiState
) {
    Column(
        modifier = modifier.padding(dimensionResource(R.dimen.p_medium))
    ) {
        Row {
            AppIconImage(
                iconUri = uiState.appIcon,
                contentDescription = uiState.appName,
                size = R.dimen.icon_large,
                modifier = Modifier
                    .size(dimensionResource(R.dimen.icon_large))
            )

            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
            Column{
                //募集ステータス
                Image(
                    painterResource(uiState.status.iconResId),
                    contentDescription = stringResource(uiState.status.labelResId),
                    modifier = Modifier.width(dimensionResource(R.dimen.icon_medium))
                )
                //アプリ名
                Text(
                    text = uiState.appName,
                    style = MaterialTheme.typography.titleMedium
                )
                //投稿日時（整形表示）
                val formattedDate = uiState.postedAt.formatDate()
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
        Text(text = "■ ${stringResource(R.string.lbl_recruit_description)}")
        Text(
            text = uiState.description,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.p_medium))
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
        TitleUrl(title = stringResource(R.string.lbl_recruit_group_url), url = uiState.groupUrl)
        TitleUrl(title = stringResource(R.string.lbl_recruit_app_url), url = uiState.appUrl)
        TitleUrl(title = stringResource(R.string.lbl_recruit_web_url), url = uiState.webUrl)
    }
}

@Preview(showBackground = true)
@Composable
fun RecruitDetailScreenPreview() {
    val data = LoadState.Success(
        RecruitUiState(
            id = "1",
            appName = "テストアプリ",
            status = RecruitStatus.Open,
            postedAt = System.currentTimeMillis(),
            description = "テスト説明",
            groupUrl = "テストグループURL",
            appUrl = "テストアプリURL",
            webUrl = "テストWEBURL"
        )
    )
    RecruitDetailScreen(recruitId = "1", uiState = data)
}