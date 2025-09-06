package com.rururi.closedtestmate.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.rururi.closedtestmate.R
import com.rururi.closedtestmate.core.ui.theme.RurustaTheme
import com.rururi.closedtestmate.recruit.domain.DetailContent

@Composable
fun Edita(
    modifier: Modifier = Modifier,
    items: List<DetailContent> = listOf(DetailContent.Text(text = "")),
    editaTitle: String = "Edita",
    label: String = "",
    onValueChange: (id:String,value:String) -> Unit = {_,_ ->},
    onAddImage: () -> Unit = {},
    onAddText: () -> Unit = {},
    onRemove: (id:String) -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.p_small))
    ){
        Box(modifier = Modifier.padding(dimensionResource(R.dimen.p_small))) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                //title
                Text(text = editaTitle,style = MaterialTheme.typography.titleMedium)

                //本体
                items.forEachIndexed { index, item ->
                    when (item) {
                        is DetailContent.Text -> {
                            OutlinedTextField(
                                value = item.text,
                                onValueChange = { onValueChange(item.id, it) },
                                label = { Text(label + (index + 1)) },
                                trailingIcon = {
                                    IconButton(onClick = {onRemove(item.id)}) {
                                        Icon(Icons.Default.Close,null)
                                    }
                                },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        is DetailContent.Image -> {
                            Box {
                                AsyncImage(
                                    model = item.uri,
                                    contentDescription = null,
                                    modifier = Modifier
                                )
                                IconButton(
                                    modifier = Modifier
                                        .align(alignment = Alignment.TopEnd)
                                        .size(dimensionResource(R.dimen.icon_small))
                                        .background(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),CircleShape),
                                    onClick = { onRemove(item.id) }
                                ) {
                                    Icon(Icons.Default.Close,null)
                                }
                            }
                        }
                    }
                }
                //EditIcon
                Row {
                    IconButton(
                        onClick = onAddImage,
                        modifier = Modifier
                            .size(dimensionResource(R.dimen.icon_medium))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = stringResource(R.string.icon_image),
                        )
                    }
                    IconButton(
                        onClick = onAddText,
                    ) {
                        Icon(
                            imageVector = Icons.Default.TextFields,
                            contentDescription = stringResource(R.string.icon_text),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditaPreview() {
    val items = listOf(
        DetailContent.Text(text = "あめんぼあかいなあいうえお"),
        DetailContent.Text(text = "普通の人は言わないよね？"),
        DetailContent.Image(uri = "https://placehold.jp/3d4070/ffffff/320x240.png".toUri())
    )
    RurustaTheme {
        Edita(items = items)
    }
}