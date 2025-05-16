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
import retrofit2.HttpException
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
                        val activeTrack = previousState.data.find { song ->
                            song.playbackState !is Song.PlaybackState.Idle
                        }

                        handleTrackClicked(intent, activeTrack)
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
        activeTrack: Song?
    ) {
        when (val playbackState = activeTrack?.playbackState) {
            null -> {
                playerManager.play(intent.trackId)
                emit(
                    SongListInternalAction.UpdateTrackState(
                        trackId = intent.trackId,
                        newState = Song.PlaybackState.Playing(progress = 0)
                    )
                )
            }

            is Song.PlaybackState.Playing -> {
                if (intent.trackId == activeTrack.id) {
                    playerManager.pause()
                    emit(
                        SongListInternalAction.UpdateTrackState(
                            trackId = intent.trackId,
                            newState = Song.PlaybackState.Paused(progress = playbackState.progress)
                        )
                    )
                } else {
                    playerManager.stop()
                    playerManager.play(intent.trackId)
                    emit(
                        SongListInternalAction.UpdateTrackState(
                            trackId = intent.trackId,
                            newState = Song.PlaybackState.Playing(progress = playbackState.progress)
                        )
                    )
                }
            }

            is Song.PlaybackState.Paused -> {
                if (intent.trackId == activeTrack.id) {
                    playerManager.resume()
                    emit(
                        SongListInternalAction.UpdateTrackState(
                            trackId = intent.trackId,
                            newState = Song.PlaybackState.Playing(progress = playbackState.progress)
                        )
                    )
                } else {
                    playerManager.stop()
                    playerManager.play(intent.trackId)
                    emit(
                        SongListInternalAction.UpdateTrackState(
                            trackId = intent.trackId,
                            newState = Song.PlaybackState.Playing(progress = 0)
                        )
                    )
                }
            }

            else -> {}
        }
    }
}