package com.zhukovskii.util

import android.net.Uri
import com.zhukovskii.util.Const.BASE_URL

object UriBuilder {

    fun buildMediaFileUri(id: Long): Uri =
        Uri.parse(
            BASE_URL + "songs/$id/play"
        )

    fun buildCoverFileUri(id: Long): Uri =
        Uri.parse(
            BASE_URL + "songs/$id/cover"
        )
}