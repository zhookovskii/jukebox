package com.zhukovskii.song_list.presentation.mvi

import android.util.Log
import com.zhukovskii.media_core.PlayerManager
import com.zhukovskii.song_list.domain.GetSongsUseCase
import com.zhukovskii.song_list.presentation.mvi.entity.SongListInternalAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SongListBootstrap @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val playerManager: PlayerManager,
) {

    fun produce(): Flow<SongListInternalAction> = retrieveDataFlow

    private val retrieveDataFlow = flow {
        playerManager.init()
        emit(SongListInternalAction.ShowLoading)

        try {
            val data = getSongsUseCase()
            emit(SongListInternalAction.BuildState(data))
        } catch (e: Exception) {
            Log.d("Bootstrap", "Error occurred while fetching data: ${e.message}")
            emit(SongListInternalAction.ShowError(e.message ?: "Unknown error"))
        }
    }
}