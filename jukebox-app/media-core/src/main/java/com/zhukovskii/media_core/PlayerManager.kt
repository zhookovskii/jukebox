package com.zhukovskii.media_core

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

interface PlayerManager {

    val progressFlow: Flow<Int>

    fun init()

    fun play(trackId: Long)

    fun pause()

    fun resume()

    fun stop()

    fun isPlaying(): Boolean

    fun seekTo(progress: Int)

    fun release()
}

@Singleton
class PlayerManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : PlayerManager {

    private var player: ExoPlayer? = null

    override val progressFlow: Flow<Int> = flow {
        while (true) {
            player?.currentPosition?.toInt()?.let { progress -> emit(progress) }
            delay(PROGRESS_UPDATE_INTERVAL)
        }
    }
    override fun init() {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
        }
    }

    override fun play(trackId: Long) {
        val url = "http://192.168.1.133:8080/songs/$trackId/play"
        val mediaItem = MediaItem.fromUri(Uri.parse(url))

        player?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    override fun pause() {
        player?.pause()
    }

    override fun resume() {
        player?.play()
    }

    override fun stop() {
        player?.stop()
    }

    override fun isPlaying() = player?.isPlaying ?: false

    override fun seekTo(progress: Int) {
        player?.seekTo(progress.toLong())
    }

    override fun release() {
        player?.release()
        player = null
    }

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL = 500L
    }
}