package com.zhukovskii.song_list.presentation.mvi

import com.zhukovskii.song_list.domain.GetSongsUseCase
import com.zhukovskii.song_list.presentation.mvi.entity.SongListIntent
import com.zhukovskii.song_list.presentation.mvi.entity.SongListInternalAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class SongListActor @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase
) {

    suspend fun process(intent: SongListIntent): Flow<SongListInternalAction> = flow {
        when (intent) {
            is SongListIntent.Retry -> {
                emit(SongListInternalAction.ShowLoading)

                try {
                    val data = getSongsUseCase()
                    emit(SongListInternalAction.BuildState(data))
                } catch (e: HttpException) {
                    emit(SongListInternalAction.ShowError(e.message ?: "Unknown error"))
                }
            }
        }
    }
}