package com.zhukovskii.song_list.domain

data class Song(
    val id: Long,
    val name: String,
    val artist: String,
    val duration: Int,
)