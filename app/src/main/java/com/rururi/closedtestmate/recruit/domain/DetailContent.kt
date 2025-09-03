package com.rururi.closedtestmate.recruit.domain

import android.net.Uri
import java.util.UUID

sealed class DetailContent(open val id:String) {
    data class Text(
        override val id: String = UUID.randomUUID().toString(),
        val text: String = ""
    ) : DetailContent(id)
    data class Image(
        override val id: String = UUID.randomUUID().toString(),
        val uri: Uri
    ) : DetailContent(id)
}