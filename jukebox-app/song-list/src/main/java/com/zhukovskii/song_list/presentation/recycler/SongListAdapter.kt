package com.zhukovskii.song_list.presentation.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.zhukovskii.song_list.R
import com.zhukovskii.song_list.domain.Song

class SongListAdapter(
    private val data: List<Song>,
    private val onClick: (Song) -> Unit,
) : ListAdapter<Song, SongItemViewHolder>(SongItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongItemViewHolder {
        return SongItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.song_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongItemViewHolder, position: Int) {
        holder.bind(data[position], onClick)
    }

    private class SongItemDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }
}