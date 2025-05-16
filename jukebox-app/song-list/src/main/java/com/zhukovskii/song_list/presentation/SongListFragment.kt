package com.zhukovskii.song_list.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zhukovskii.media_core.CoverLoader
import com.zhukovskii.media_core.PlayerManager
import com.zhukovskii.song_list.R
import com.zhukovskii.song_list.presentation.mvi.SongListViewModel
import com.zhukovskii.song_list.presentation.mvi.entity.SongListIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SongListFragment : Fragment(R.layout.song_list_fragment) {

    private val viewModel: SongListViewModel by activityViewModels()

    @Inject
    lateinit var playerManager: PlayerManager

    @Inject
    lateinit var coverLoader: CoverLoader

    private val progressUpdateScope = CoroutineScope(Dispatchers.Main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SongListView(
            view = view,
            onLoadCover = { trackId, imageView ->
                coverLoader.load(trackId, imageView)
            },
            onItemClick = { song ->
                progressUpdateScope.clear()
                viewModel.accept(SongListIntent.TrackClicked(song.id))
            },
            onProgressChanged = { progress ->
                viewModel.accept(SongListIntent.ProgressChanged(progress))
            },
            onRetryClick = {
                viewModel.accept(SongListIntent.Retry)
            },
            onProgressUpdateCallbackSet = { callback ->
                launchWhenCreated {
                    playerManager.progressFlow
                        .stateIn(progressUpdateScope)
                        .collectLatest { progress -> callback(progress) }
                }
            }
        ).run {
            launchWhenCreated {
                viewModel.state.collectLatest(::render)
            }
        }
    }

    private fun launchWhenCreated(block: suspend () -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch { block() }
            }
        }
    }

    private fun CoroutineScope.clear() {
        coroutineContext.cancelChildren()
    }

    override fun onDestroy() {
        progressUpdateScope.cancel()
        super.onDestroy()
    }

    companion object {
        fun newInstance() = SongListFragment()
    }
}