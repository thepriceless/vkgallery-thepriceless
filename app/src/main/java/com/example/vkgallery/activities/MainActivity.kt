package com.example.vkgallery.activities

import android.R.attr.value
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.vkgallery.models.AlbumShow
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setContentView(R.layout.activity_main)*/

        val authLauncher = VK.login(this) { result : VKAuthenticationResult ->
            when (result) {
                is VKAuthenticationResult.Success -> {
                    Log.e("TAG", "nice")
                    val myIntent = Intent(this, AlbumShow::class.java)
                    myIntent.putExtra("key", value)

                    this.startActivity(myIntent)
                }
                is VKAuthenticationResult.Failed -> {
                    Log.e("TAG", "not nice")
                }
            }
        }

        authLauncher.launch(arrayListOf(VKScope.WALL, VKScope.PHOTOS, VKScope.FRIENDS))
    }
}