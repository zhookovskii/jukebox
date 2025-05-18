package com.zhukovskii.media_core

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.zhukovskii.util.UriBuilder
import javax.inject.Inject
import javax.inject.Singleton

interface CoverLoader {

    fun load(trackId: Long, imageView: ImageView)
}

@Singleton
class CoverLoaderImpl @Inject constructor() : CoverLoader {

    override fun load(trackId: Long, imageView: ImageView) {
        Glide.with(imageView)
            .load(UriBuilder.buildCoverFileUri(trackId))
            .placeholder(R.drawable.cover_placeholder)
            .into(imageView)
    }
}

