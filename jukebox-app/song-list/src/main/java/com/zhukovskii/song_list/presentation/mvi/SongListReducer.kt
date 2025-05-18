package com.zhukovskii.song_list.presentation.mvi

import com.zhukovskii.song_list.domain.Song
import com.zhukovskii.song_list.presentation.mvi.entity.SongListInternalAction
import com.zhukovskii.song_list.presentation.mvi.entity.SongListState
import javax.inject.Inject

class SongListReducer @Inject constructor() {

    fun reduce(
        internalAction: SongListInternalAction,
        previousState: SongListState
    ): SongListState {
        return when (internalAction) {
            is SongListInternalAction.ShowLoading -> SongListState.Loading
            is SongListInternalAction.BuildState -> SongListState.Content(internalAction.data)
            is SongListInternalAction.ShowError -> SongListState.Error(internalAction.message)
            is SongListInternalAction.UpdateTrackState -> handlePlaybackStateUpdate(
                trackId = internalAction.trackId,
                newState = internalAction.newState,
                previousState = previousState,
            )
        }
    }

    private fun handlePlaybackStateUpdate(
        trackId: Long,
        newState: Song.PlaybackState,
        previousState: SongListState
    ): SongListState {
        return when (previousState) {
            is SongListState.Content -> previousState.copy(
                data = previousState.data.map { song ->
                    if (song.id == trackId)
                        song.copy(playbackState = newState)
                    else
                        song.copy(playbackState = Song.PlaybackState.IDLE)
                }
            )

            else -> previousState
        }
    }
}