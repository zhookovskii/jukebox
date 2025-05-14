package com.zhukovskii.song_list.presentation.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zhukovskii.song_list.R
import com.zhukovskii.song_list.domain.Song

class SongItemViewHolder(
    view: View,
) : RecyclerView.ViewHolder(view) {

    private val rootView = view
    private val titleView: TextView = view.findViewById(R.id.title)
    private val artistView: TextView = view.findViewById(R.id.artist)
    private val durationView: TextView = view.findViewById(R.id.duration)

    fun bind(song: Song, onClick: (Song) -> Unit) {
        titleView.text = song.name
        artistView.text = song.artist
        durationView.text = song.duration.toSongDurationText()

        rootView.setOnClickListener { onClick(song) }
    }

    private fun Int.toSongDurationText(): String {
        val minutes = "${this / 60}".justifyLeft(2, "0")
        val seconds = "${this % 60}".justifyLeft(2, "0")
        return "$minutes:$seconds"
    }

    private fun String.justifyLeft(requiredLength: Int, fillSymbol: CharSequence): String {
        if (length >= requiredLength) return this

        return fillSymbol.repeat(requiredLength - length) + this
    }
}