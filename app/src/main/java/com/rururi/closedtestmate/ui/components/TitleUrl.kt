package com.rururi.closedtestmate.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.rururi.closedtestmate.R

@Composable
fun TitleUrl(
    modifier: Modifier = Modifier,
    title: String,
    url: String,
) {
    val uriHandler = LocalUriHandler.current

    Column(modifier = modifier) {
        Text(
            text = "■ $title",
            style = MaterialTheme.typography.titleMedium
        )
        BasicText(
            text = url.toAnnotatedString(url, MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .padding(start = dimensionResource(R.dimen.p_medium))
                .clickable {
                    uriHandler.openUri(url)
                },
        )
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.p_small)))
    }
}

@Preview(showBackground = true)
@Composable
fun TitleUrlPreview() {
    TitleUrl(
        title = "テストタイトル",
        url = "テストURL",
    )
}