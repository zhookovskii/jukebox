package com.zhukovskii.media_core.di

import com.zhukovskii.media_core.CoverLoader
import com.zhukovskii.media_core.CoverLoaderImpl
import com.zhukovskii.media_core.PlayerManager
import com.zhukovskii.media_core.PlayerManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [MediaModule.Declarations::class])
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Module
    @InstallIn(SingletonComponent::class)
    interface Declarations {

        @Binds
        @Singleton
        fun bindsPlayerManager(impl: PlayerManagerImpl): PlayerManager

        @Binds
        @Singleton
        fun bindsCoverLoader(impl: CoverLoaderImpl): CoverLoader
    }
}