package com.zhukovskii.song_list.presentation.mvi.entity

sealed class SongListIntent {
    data class TrackClicked(val trackId: Long) : SongListIntent()
    data class ProgressChanged(val progress: Int) : SongListIntent()
    data object Retry : SongListIntent()
}