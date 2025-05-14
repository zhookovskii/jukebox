package com.zhukovskii.song_list.presentation.mvi.entity

sealed class SongListIntent {
    data object Retry : SongListIntent()
}