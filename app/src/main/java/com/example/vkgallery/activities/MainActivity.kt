package com.example.vkgallery.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authLauncher = VK.login(this) { result : VKAuthenticationResult ->
            when (result) {
                is VKAuthenticationResult.Success -> {
                    Log.e("TAG", "nice")
                    this.startActivity(Intent(this, AlbumShowActivity::class.java))
                }
                is VKAuthenticationResult.Failed -> {
                    Log.e("TAG", "not nice")
                }
            }
        }

        authLauncher.launch(arrayListOf(VKScope.PHOTOS))
    }
}