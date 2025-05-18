package com.zhukovskii.song_list.presentation.mvi.entity

import com.zhukovskii.util.Milliseconds

sealed class SongListIntent {
    data class TrackClicked(val trackId: Long) : SongListIntent()
    data class ProgressChanged(val progress: Milliseconds) : SongListIntent()
    data object Retry : SongListIntent()
}