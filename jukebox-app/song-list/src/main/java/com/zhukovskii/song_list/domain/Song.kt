package com.zhukovskii.song_list.domain

data class Song(
    val id: Long,
    val name: String,
    val artist: String,
    val duration: Int,
    val playbackState: PlaybackState
) {

    sealed class PlaybackState {
        data class Playing(val progress: Int) : PlaybackState()
        data class Paused(val progress: Int) : PlaybackState()
        data object Idle : PlaybackState()

        fun copyWithProgress(progress: Int): PlaybackState {
            return when (this) {
                is Idle, is Paused -> this
                is Playing -> Playing(progress)
            }
        }
    }
}