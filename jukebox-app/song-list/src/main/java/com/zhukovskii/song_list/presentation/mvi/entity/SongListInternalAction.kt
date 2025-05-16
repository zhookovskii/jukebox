package com.zhukovskii.song_list.presentation.mvi.entity

import com.zhukovskii.song_list.domain.Song

sealed class SongListInternalAction {

    data object ShowLoading : SongListInternalAction()

    data class ShowError(val message: String) : SongListInternalAction()

    data class BuildState(
        val data: List<Song>
    ) : SongListInternalAction()

    data class UpdateTrackState(
        val trackId: Long,
        val newState: Song.PlaybackState,
    ) : SongListInternalAction()

    data class UpdateCurrentTrackProgress(
        val progress: Int
    ) : SongListInternalAction()
}