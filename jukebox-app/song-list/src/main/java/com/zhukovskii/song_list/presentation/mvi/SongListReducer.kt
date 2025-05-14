package com.zhukovskii.song_list.presentation.mvi

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
        }
    }
}