package com.zhukovskii.song_list.data

import com.zhukovskii.song_list.domain.Song

interface SongListRepository {

    suspend fun getSongs(): List<Song>
}