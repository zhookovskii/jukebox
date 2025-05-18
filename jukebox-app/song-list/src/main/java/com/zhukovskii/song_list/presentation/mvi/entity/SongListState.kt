package com.zhukovskii.song_list.presentation.mvi.entity

import com.zhukovskii.song_list.domain.Song

sealed class SongListState {

    data class Content(val data: List<Song>) : SongListState() {

        val currentTrack: Song?
            get() = data.find { it.playbackState != Song.PlaybackState.IDLE }
    }

    data class Error(val message: String) : SongListState()

    data object Loading : SongListState()

    companion object {
        val INITIAL = Content(emptyList())
    }
}