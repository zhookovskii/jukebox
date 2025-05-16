package com.zhukovskii.media_core

import android.widget.ImageView
import com.bumptech.glide.Glide
import javax.inject.Inject
import javax.inject.Singleton

interface CoverLoader {

    fun load(trackId: Long, imageView: ImageView)
}

@Singleton
class CoverLoaderImpl @Inject constructor() : CoverLoader {

    override fun load(trackId: Long, imageView: ImageView) {
        Glide.with(imageView)
            .load("http://192.168.1.133:8080/songs/$trackId/cover")
            .placeholder(R.drawable.cover_placeholder)
            .into(imageView)
    }
}

