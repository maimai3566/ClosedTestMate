package com.rururi.closedtestmate.core.ui

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rururi.closedtestmate.R

enum class IconShape {
    PlainRect,  //そのまま
    Rounded,    //角丸
    Circle      //円形
}
@Composable
fun AppIconImage(
    modifier: Modifier = Modifier,
    iconUri: Uri? = null,   //選択したアイコンの場所
    onImageSelected: ((Uri) -> Unit)? = null,   //アイコンをクリックしたときの処理
    contentDescription: String, //アイコンの名前
    size: Int = R.dimen.icon_extra_large,           //アイコンの大きさ
    shape: IconShape = IconShape.PlainRect,
    border: Dp = dimensionResource(R.dimen.p_none),
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected?.invoke(it) }
    }
    val clipShape: Shape = when (shape) {
        IconShape.Circle -> CircleShape
        IconShape.Rounded -> RoundedCornerShape(dimensionResource(R.dimen.p_medium))
        IconShape.PlainRect -> RectangleShape
    }
    val contentScale = when (shape) {
        IconShape.Circle -> ContentScale.Crop   //円の時だけ切り抜き
        else -> ContentScale.Fit                //円以外は元比率で収める
    }

    Log.d("AppIconImage", "iconUri: $iconUri")
    Surface(
        shape = clipShape,
        color = Color.Transparent,
        border = if (border > 0.dp) BorderStroke(border, MaterialTheme.colorScheme.primary) else null,
        onClick = {
            if (onImageSelected != null) {
                {launcher.launch("image/*")}
            } else null
        },
        modifier = modifier.size(dimensionResource(size))
    ){
        if(iconUri == null){
            Image(
                painter = painterResource(R.drawable.no_icon),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            AsyncImage(
                model = iconUri,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppIconImagePreview() {
    AppIconImage(contentDescription = "Preview")
}
