package com.zhukovskii.song_list.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zhukovskii.song_list.R
import com.zhukovskii.song_list.presentation.mvi.SongListViewModel
import com.zhukovskii.song_list.presentation.mvi.entity.SongListIntent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SongListFragment : Fragment(R.layout.song_list_fragment) {

    private val viewModel: SongListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SongListView(
            view = view,
            onItemClick = { viewModel.accept(SongListIntent.Retry) },
            onRetryClick = { viewModel.accept(SongListIntent.Retry) },
        ).run {
            launchWhenCreated {
                viewModel.state.collectLatest { state ->
                    render(state)
                }
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

    companion object {
        fun newInstance() = SongListFragment()
    }
}