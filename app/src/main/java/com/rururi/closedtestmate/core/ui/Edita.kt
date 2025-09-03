package com.rururi.closedtestmate.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
    layout: EditaLayout = EditaLayout.CompactInLine,    //アイコンの並べ方など
    maxHeight: Dp = dimensionResource(R.dimen.edita_max_height),    //合間に入っても邪魔にならないサイズ240dp
) {
    val listState = rememberLazyListState()     //LazyColumnのスクロールの位置や状態を保持
    val contentPaddingBottom = when(layout) {
        EditaLayout.CompactInLine -> 0.dp
        EditaLayout.BottomBar -> {
            if(items.size>3) dimensionResource(R.dimen.icon_medium) else dimensionResource(R.dimen.p_small)
        }
        EditaLayout.Fab -> 0.dp
    }

    Card(
        modifier = modifier
            .fillMaxWidth()        ,
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.p_small))
    ){
        Box(modifier = Modifier.padding(dimensionResource(R.dimen.p_small))
        ){
            Column {
                Text(text = editaTitle)
                if(layout == EditaLayout.CompactInLine) {
                    //ヘッダにアイコンを置く
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(label, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onAddImage) { Icon(Icons.Default.Image,null)}
                        IconButton(onClick = onAddText) { Icon(Icons.Default.TextFields,null)}
                    }
                }

                //本体（内部スクロール）
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = dimensionResource(R.dimen.edita_min_height), max = maxHeight),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.p_small)),
                    contentPadding = PaddingValues(bottom = contentPaddingBottom)
                ) {
                    itemsIndexed(
                        items = items,
                        key = { _, item -> item.id }
                    ) { index, item ->
                        when (item) {
                            is DetailContent.Text -> {
                                val value = item.text
                                OutlinedTextField(
                                    value = value,
                                    onValueChange = { onValueChange(item.id, it) },
                                    label = { Text(label + (index + 1)) },
                                    trailingIcon = {
                                        IconButton(onClick = {onRemove(item.id)}) {
                                            Icon(Icons.Default.Close,null)
                                        }
                                    },
                                    minLines = 3,
                                    modifier = Modifier .fillMaxWidth()
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
                }
                //BottomBarレイアウト
                if(layout == EditaLayout.BottomBar) {
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

            //右下FAB
            if(layout == EditaLayout.Fab){
                SmallFloatingActionButton(
                    onClick = onAddText,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ){ Icon(Icons.Default.TextFields,null) }
            }
        }
    }
}

//エディタのレイアウト
enum class EditaLayout {
    CompactInLine,  //ラベル＋小アイコン
    BottomBar,      //最下部にアイコン（横いっぱいに広がる？）
    Fab             //右下に小アイコン
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
        Edita(items = items, layout = EditaLayout.BottomBar)
    }
}
