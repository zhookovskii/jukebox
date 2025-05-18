package com.zhukovskii.song_list.presentation.mvi

import com.zhukovskii.media_core.PlayerManager
import com.zhukovskii.song_list.domain.GetSongsUseCase
import com.zhukovskii.song_list.domain.Song
import com.zhukovskii.song_list.presentation.mvi.entity.SongListIntent
import com.zhukovskii.song_list.presentation.mvi.entity.SongListInternalAction
import com.zhukovskii.song_list.presentation.mvi.entity.SongListState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SongListActor @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val playerManager: PlayerManager,
) {

    suspend fun process(
        intent: SongListIntent,
        previousState: SongListState
    ): Flow<SongListInternalAction> = flow {
        when (intent) {
            is SongListIntent.Retry -> {
                emit(SongListInternalAction.ShowLoading)

                try {
                    val data = getSongsUseCase()
                    emit(SongListInternalAction.BuildState(data))
                } catch (e: Exception) {
                    emit(SongListInternalAction.ShowError(e.message ?: "Unknown error"))
                }
            }

            is SongListIntent.TrackClicked -> {
                when (previousState) {
                    is SongListState.Content -> {
                        val currentTrack = previousState.currentTrack
                        handleTrackClicked(intent, currentTrack)
                    }

                    else -> {}
                }
            }

            is SongListIntent.ProgressChanged -> {
                playerManager.seekTo(intent.progress)
            }
        }
    }

    private suspend fun FlowCollector<SongListInternalAction>.handleTrackClicked(
        intent: SongListIntent.TrackClicked,
        currentTrack: Song?
    ) {
        if (currentTrack?.id == intent.trackId) {
            when (currentTrack.playbackState) {
                Song.PlaybackState.PLAYING -> {
                    playerManager.pause()

                    emit(
                        SongListInternalAction.UpdateTrackState(
                            trackId = intent.trackId,
                            newState = Song.PlaybackState.PAUSED
                        )
                    )
                }

                Song.PlaybackState.PAUSED -> {
                    playerManager.resume()

                    emit(
                        SongListInternalAction.UpdateTrackState(
                            trackId = intent.trackId,
                            newState = Song.PlaybackState.PLAYING
                        )
                    )
                }

                else -> {}
            }
        } else {
            if (currentTrack != null) playerManager.stop()
            playerManager.play(intent.trackId)

            emit(
                SongListInternalAction.UpdateTrackState(
                    trackId = intent.trackId,
                    newState = Song.PlaybackState.PLAYING
                )
            )
        }
    }
}