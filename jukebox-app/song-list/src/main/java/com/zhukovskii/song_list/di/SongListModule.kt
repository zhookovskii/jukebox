package com.zhukovskii.song_list.di

import com.zhukovskii.song_list.data.SongListApi
import com.zhukovskii.song_list.data.SongListRepository
import com.zhukovskii.song_list.domain.SongListRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [SongListModule.Declarations::class])
@InstallIn(SingletonComponent::class)
object SongListModule {

    @Provides
    @Singleton
    fun provideSongListApi(retrofit: Retrofit): SongListApi =
        retrofit.create(SongListApi::class.java)

    @Module
    @InstallIn(SingletonComponent::class)
    interface Declarations {

        @Binds
        @Singleton
        fun bindSongListRepository(impl: SongListRepositoryImpl): SongListRepository
    }
}