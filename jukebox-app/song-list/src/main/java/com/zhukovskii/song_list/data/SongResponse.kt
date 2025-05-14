package com.zhukovskii.song_list.data

data class SongResponse(
    val id: Long,
    val name: String,
    val artist: String,
    val duration: Int,
)