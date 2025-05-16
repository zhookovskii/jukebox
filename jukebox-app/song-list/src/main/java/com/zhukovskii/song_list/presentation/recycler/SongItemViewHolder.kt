package com.zhukovskii.song_list.presentation.recycler

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.zhukovskii.song_list.R
import com.zhukovskii.song_list.domain.Song

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
        onProgressChanged: (Int) -> Unit
    ) {
        titleView.text = song.name
        artistView.text = song.artist
        durationView.text = song.duration.toSongDurationText()

        onLoadCover(song.id, coverView)

        view.setOnClickListener { onClick(song) }

        when (val playbackState = song.playbackState) {
            Song.PlaybackState.Idle -> {
                titleView.setTextColorById(R.color.primary, context)
                artistView.setTextColorById(R.color.primary_variant, context)
                durationView.setTextColorById(R.color.primary_variant, context)
                view.setBackgroundColorById(R.color.secondary, context)
                seekBar.visibility = View.GONE
                iconView.setTintedDrawableById(R.drawable.play_icon, R.color.primary, context)
            }

            else -> {
                titleView.setTextColorById(R.color.secondary, context)
                artistView.setTextColorById(R.color.primary_variant_dark, context)
                durationView.setTextColorById(R.color.primary_variant_dark, context)
                view.setBackgroundColorById(R.color.primary, context)
                seekBar.visibility = View.VISIBLE

                seekBar.max = song.duration
                when (playbackState) {
                    is Song.PlaybackState.Playing -> {
                        iconView.setTintedDrawableById(R.drawable.pause_icon, R.color.secondary, context)
                    }
                    is Song.PlaybackState.Paused -> {
                        iconView.setTintedDrawableById(R.drawable.play_icon, R.color.secondary, context)
                    }
                    else -> {}
                }

                seekBar.setOnSeekBarChangeListener(
                    object : OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
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

    fun onProgressUpdate(song: Song, progress: Int) {
        if (song.playbackState !is Song.PlaybackState.Idle) {
            seekBar.progress = progress
            durationView.text = progress.toSongDurationText()
        }
    }

    private fun Int.toSongDurationText(): String {
        val durationSeconds = this / 1000
        val minutes = "${durationSeconds / 60}".justifyLeft(2, "0")
        val seconds = "${durationSeconds % 60}".justifyLeft(2, "0")
        return "$minutes:$seconds"
    }

    private fun String.justifyLeft(requiredLength: Int, fillSymbol: CharSequence): String {
        if (length >= requiredLength) return this

        return fillSymbol.repeat(requiredLength - length) + this
    }

    private fun TextView.setTextColorById(resourceId: Int, context: Context) {
        setTextColor(ContextCompat.getColor(context, resourceId))
    }

    private fun View.setBackgroundColorById(resourceId: Int, context: Context) {
        setBackgroundColor(ContextCompat.getColor(context, resourceId))
    }

    private fun ImageView.setTintedDrawableById(drawableResId: Int, colorResId: Int, context: Context) {
        ContextCompat.getDrawable(context, drawableResId)?.let { drawable ->
            val wrappedDrawable = DrawableCompat.wrap(drawable)
            val tint = ContextCompat.getColor(context, colorResId)

            DrawableCompat.setTint(wrappedDrawable, tint)
            setImageDrawable(wrappedDrawable)
        }
    }
}