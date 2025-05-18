package com.zhukovskii.song_list.presentation.recycler

import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zhukovskii.song_list.R
import com.zhukovskii.song_list.domain.Song
import com.zhukovskii.util.Milliseconds
import com.zhukovskii.util.setBackgroundColorById
import com.zhukovskii.util.setTextColorById
import com.zhukovskii.util.setTintedDrawableById
import com.zhukovskii.util.toMilliseconds
import com.zhukovskii.util.toSeconds
import com.zhukovskii.util.toSongDurationText
import com.zhukovskii.design_core.R as design_R

class SongItemViewHolder(
    private val view: View,
) : RecyclerView.ViewHolder(view) {

    private val context = view.context

    private val iconView: ImageView = view.findViewById(R.id.icon)
    private val coverView: ImageView = view.findViewById(R.id.cover)
    private val titleView: TextView = view.findViewById(R.id.title)
    private val artistView: TextView = view.findViewById(R.id.artist)
    private val durationView: TextView = view.findViewById(R.id.duration)
    private val seekBar: SeekBar = view.findViewById(R.id.seekbar)

    fun bind(
        song: Song,
        onLoadCover: (trackId: Long, imageView: ImageView) -> Unit,
        onClick: (Song) -> Unit,
        onProgressChanged: (Milliseconds) -> Unit
    ) {
        titleView.text = song.name
        artistView.text = song.artist
        durationView.text = song.duration.toSongDurationText()

        onLoadCover(song.id, coverView)

        view.setOnClickListener { onClick(song) }

        when (val playbackState = song.playbackState) {
            Song.PlaybackState.IDLE -> {
                titleView.setTextColorById(design_R.color.primary, context)
                artistView.setTextColorById(design_R.color.primary_variant, context)
                durationView.setTextColorById(design_R.color.primary_variant, context)
                view.setBackgroundColorById(design_R.color.secondary, context)
                seekBar.visibility = View.GONE
                iconView.setTintedDrawableById(
                    R.drawable.play_icon,
                    design_R.color.primary,
                    context
                )
            }

            else -> {
                titleView.setTextColorById(design_R.color.secondary, context)
                artistView.setTextColorById(design_R.color.primary_variant_dark, context)
                durationView.setTextColorById(design_R.color.primary_variant_dark, context)
                view.setBackgroundColorById(design_R.color.primary, context)
                seekBar.visibility = View.VISIBLE

                seekBar.max = song.duration.toMilliseconds()
                when (playbackState) {
                    Song.PlaybackState.PLAYING -> {
                        iconView.setTintedDrawableById(
                            R.drawable.pause_icon,
                            design_R.color.secondary,
                            context
                        )
                    }

                    Song.PlaybackState.PAUSED -> {
                        iconView.setTintedDrawableById(
                            R.drawable.play_icon,
                            design_R.color.secondary,
                            context
                        )
                    }

                    else -> {}
                }

                seekBar.setOnSeekBarChangeListener(
                    object : OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Milliseconds,
                            fromUser: Boolean
                        ) {
                            if (fromUser) {
                                onProgressChanged(progress)
                            }
                        }

                        override fun onStartTrackingTouch(seekbar: SeekBar?) {}

                        override fun onStopTrackingTouch(seekbar: SeekBar?) {}

                    }
                )
            }
        }
    }

    fun onProgressUpdate(song: Song, progress: Milliseconds) {
        if (song.playbackState != Song.PlaybackState.IDLE) {
            seekBar.progress = progress
            durationView.text = progress.toSeconds().toSongDurationText()
        }
    }
}