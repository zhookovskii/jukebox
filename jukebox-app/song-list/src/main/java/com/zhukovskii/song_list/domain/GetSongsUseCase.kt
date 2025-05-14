package com.zhukovskii.song_list.domain

import com.zhukovskii.song_list.data.SongListRepository
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val repository: SongListRepository,
) {

    suspend operator fun invoke() = repository.getSongs()
}