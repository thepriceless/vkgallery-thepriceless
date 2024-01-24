package com.example.vkgallery.models

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vkgallery.R
import com.example.vkgallery.models.ui.main.AlbumShowFragment
import com.example.vkgallery.models.ui.main.MainViewModel
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.sdk.api.photos.PhotosService
import com.vk.sdk.api.photos.dto.PhotosGetAlbumsResponseDto
import kotlinx.coroutines.launch


class AlbumShow : AppCompatActivity() {
    private lateinit var albumsAdapter: AlbumAdapter
    private lateinit var albumsList: MutableList<Album>
    private lateinit var albumsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_show)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AlbumShowFragment.newInstance())
                .commitNow()
        }

        val activity = this

        val viewModel: MainViewModel by viewModels()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rollDice()
                VK.execute(PhotosService().photosGetAlbums(needCovers = true), object:
                    VKApiCallback<PhotosGetAlbumsResponseDto> {
                    override fun success(result: PhotosGetAlbumsResponseDto) {
                        albumsList = mutableListOf()
                        for (album in result.items) {
                            album.thumbSrc?.let { Album(it, album.title, album.id) }
                                ?.let { albumsList.add(it) }
                        }
                        Log.i("size", albumsList.size.toString())
                        albumsRecyclerView = findViewById(R.id.album_view)
                        albumsRecyclerView.layoutManager = GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false)
                        albumsAdapter = AlbumAdapter(activity, albumsList)
                        albumsRecyclerView.adapter = albumsAdapter
                    }
                    override fun fail(error: Exception) {
                        Log.e(ContentValues.TAG, error.toString())
                    }
                })
            }
        }
    }
}