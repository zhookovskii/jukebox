package com.zhukovskii.song_list.data

import com.zhukovskii.song_list.domain.Song
import javax.inject.Inject

class SongListConverter @Inject constructor() {

    fun convert(songs: List<SongResponse>): List<Song> {
        return songs.map {
            Song(
                id = it.id,
                name = it.name,
                artist = it.artist,
                duration = it.duration,
            )
        }
    }
}