package com.zhukovskii.song_list.data

import retrofit2.http.GET

interface SongListApi {

    @GET("songs")
    suspend fun getSongs(): List<SongResponse>
}