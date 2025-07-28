package com.rururi.closedtestmate.ui.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rururi.closedtestmate.R

@Composable
fun AppIconImage(
    modifier: Modifier = Modifier,
    iconUri: Uri? = null,
    onImageSelected: (Uri) -> Unit = {},
    contentDescription: String,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    Box(
        modifier = modifier
            .clickable { launcher.launch("image/*") },
        contentAlignment = Alignment.Center
    ){
        if(iconUri == null){
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize()
            )
            } else {
            AsyncImage(
                model = iconUri,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
