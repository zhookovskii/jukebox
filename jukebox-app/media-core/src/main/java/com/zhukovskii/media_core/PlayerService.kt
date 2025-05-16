package com.zhukovskii.media_core

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerService : MediaLibraryService() {

    private lateinit var player: ExoPlayer

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
    }
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return MediaLibrarySession.Builder(
            this,
            player,
            object : MediaLibrarySession.Callback {}
        ).build()
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}