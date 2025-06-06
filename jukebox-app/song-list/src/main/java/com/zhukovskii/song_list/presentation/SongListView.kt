package com.zhukovskii.song_list.presentation

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.zhukovskii.song_list.R
import com.zhukovskii.song_list.domain.Song
import com.zhukovskii.song_list.presentation.mvi.entity.SongListState
import com.zhukovskii.song_list.presentation.recycler.SongListAdapter
import com.zhukovskii.util.Milliseconds
import com.zhukovskii.util.hide
import com.zhukovskii.util.show

class SongListView(
    view: View,
    onLoadCover: (trackId: Long, imageView: ImageView) -> Unit,
    onItemClick: (Song) -> Unit,
    onProgressChanged: (Milliseconds) -> Unit,
    onRetryClick: () -> Unit,
    onProgressUpdateCallbackSet: ((Int) -> Unit) -> Unit,
) {

    private val contentView: LinearLayout = view.findViewById(R.id.content_layout)
    private val loadingView: LinearLayout = view.findViewById(R.id.loading_layout)
    private val errorView: LinearLayout = view.findViewById(R.id.error_layout)
    private val listView: RecyclerView = view.findViewById(R.id.list)
    private val retryButtonView: MaterialButton = view.findViewById(R.id.retry_button)

    private val adapter = SongListAdapter(
        onLoadCover = onLoadCover,
        onClick = onItemClick,
        onProgressChanged = onProgressChanged,
        onProgressUpdateCallbackSet = onProgressUpdateCallbackSet,
    )

    init {
        listView.layoutManager = LinearLayoutManager(view.context)
        listView.adapter = adapter

        retryButtonView.setOnClickListener { onRetryClick() }
    }

    fun render(state: SongListState) {
        Log.d("SongListView", "New state: $state")
        when (state) {
            is SongListState.Content -> {
                loadingView.hide()
                errorView.hide()

                adapter.submitList(state.data)
                contentView.show()
            }

            is SongListState.Loading -> {
                contentView.hide()
                errorView.hide()

                loadingView.show()
            }

            is SongListState.Error -> {
                contentView.hide()
                loadingView.hide()

                errorView.show()
            }
        }
    }
}