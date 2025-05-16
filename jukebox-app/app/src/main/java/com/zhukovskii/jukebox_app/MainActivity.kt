package com.zhukovskii.jukebox_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zhukovskii.song_list.presentation.SongListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_view, SongListFragment.newInstance())
            .commit()
    }
}