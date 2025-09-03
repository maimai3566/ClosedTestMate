package com.rururi.closedtestmate.ui.recruitdetail

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
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.core.ui.AppIconImage
import com.rururi.closedtestmate.core.ui.ErrorScreen
import com.rururi.closedtestmate.core.ui.LoadingScreen
import com.rururi.closedtestmate.core.ui.TitleUrl
import com.rururi.closedtestmate.core.util.formatDate
import com.rururi.closedtestmate.recruit.domain.RecruitStatus

@Composable
fun RecruitDetailScreen(
    modifier: Modifier = Modifier,
    uiState: RecruitDetailUiState
){
    when {
        uiState.isLoading -> {
            LoadingScreen(modifier = modifier)
        }
        uiState.error != null -> {
            ErrorScreen(modifier = modifier, error = uiState.error)
        }
        uiState.detail != null -> {
            SuccessScreen(modifier = modifier, uiState = uiState.detail)
        }
        else -> {
            ErrorScreen(modifier = modifier, error = Exception("Unknown Error"))
        }
    }
}

@Composable
fun SuccessScreen(
    modifier: Modifier = Modifier,
    uiState: RecruitDetailItem
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
        Text(text = "■ ${stringResource(R.string.lbl_recruit_details)}")

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
        TitleUrl(title = stringResource(R.string.lbl_recruit_group_url), url = uiState.groupUrl)
        TitleUrl(title = stringResource(R.string.lbl_recruit_app_url), url = uiState.appUrl)
        TitleUrl(title = stringResource(R.string.lbl_recruit_web_url), url = uiState.webUrl)
    }
}


@Preview(showBackground = true)
@Composable
fun RecruitDetailScreenPreview() {
    val item = RecruitDetailItem(
            id = "1",
            appName = "テストアプリ",
            status = RecruitStatus.Open,
            postedAt = System.currentTimeMillis(),
//            description = "テスト説明",
            groupUrl = "テストグループURL",
            appUrl = "テストアプリURL",
            webUrl = "テストWEBURL"

    )
    val state = RecruitDetailUiState(detail = item)
    RecruitDetailScreen(uiState = state)
}