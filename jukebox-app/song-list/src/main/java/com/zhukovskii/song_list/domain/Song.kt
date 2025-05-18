package com.zhukovskii.song_list.domain

import com.zhukovskii.util.Seconds

data class Song(
    val id: Long,
    val name: String,
    val artist: String,
    val duration: Seconds,
    val playbackState: PlaybackState
) {

    enum class PlaybackState {
        PLAYING, PAUSED, IDLE
    }
}