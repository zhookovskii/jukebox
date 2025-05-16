package com.zhukovskii.song_list.presentation.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.zhukovskii.song_list.R
import com.zhukovskii.song_list.domain.Song

class SongListAdapter(
    private val onLoadCover: (trackId: Long, imageView: ImageView) -> Unit,
    private val onClick: (Song) -> Unit,
    private val onProgressChanged: (Int) -> Unit,
    private val onProgressUpdateCallbackSet: ((Int) -> Unit) -> Unit,
) : ListAdapter<Song, SongItemViewHolder>(SongItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongItemViewHolder {
        return SongItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.song_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongItemViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song, onLoadCover, onClick, onProgressChanged)
        onProgressUpdateCallbackSet { progress ->
            holder.onProgressUpdate(song, progress)
        }
    }

    private class SongItemDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }
}