package com.zhukovskii.song_list.domain

import com.zhukovskii.song_list.data.SongListApi
import com.zhukovskii.song_list.data.SongListConverter
import com.zhukovskii.song_list.data.SongListRepository
import javax.inject.Inject

class SongListRepositoryImpl @Inject constructor(
    private val api: SongListApi,
    private val converter: SongListConverter,
) : SongListRepository {
    override suspend fun getSongs(): List<Song> {
        return converter.convert(api.getSongs())
    }
}