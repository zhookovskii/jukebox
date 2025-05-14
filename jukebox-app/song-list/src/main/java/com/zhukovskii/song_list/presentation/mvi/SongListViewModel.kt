package com.zhukovskii.song_list.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhukovskii.song_list.presentation.mvi.entity.SongListIntent
import com.zhukovskii.song_list.presentation.mvi.entity.SongListInternalAction
import com.zhukovskii.song_list.presentation.mvi.entity.SongListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongListViewModel @Inject constructor(
    private val actor: SongListActor,
    private val bootstrap: SongListBootstrap,
    private val reducer: SongListReducer,
) : ViewModel() {

    private val _stateFlow: MutableStateFlow<SongListState> = MutableStateFlow(SongListState.INITIAL)
    val state: StateFlow<SongListState> = _stateFlow

    private val intentFlow: MutableSharedFlow<SongListIntent> = MutableSharedFlow()

    init {
        startViewModel()
    }

    private fun startViewModel() {
        bootstrap.produce()
            .onEach(::handleInternalAction)
            .launchIn(viewModelScope)

        intentFlow
            .onEach(::handleIntent)
            .launchIn(viewModelScope)
    }

    private suspend fun handleIntent(intent: SongListIntent) {
        actor.process(intent)
            .onEach(::handleInternalAction)
            .launchIn(viewModelScope)
    }

    private suspend fun handleInternalAction(internalAction: SongListInternalAction) {
        _stateFlow.emit(
            reducer.reduce(internalAction, _stateFlow.value)
        )
    }

    fun accept(intent: SongListIntent) {
        viewModelScope.launch { intentFlow.emit(intent) }
    }
}