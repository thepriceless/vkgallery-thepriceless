package com.example.vkgallery.models.ui.main

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.vkgallery.dto.User
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.sdk.api.account.AccountService
import com.vk.sdk.api.photos.PhotosService
import com.vk.sdk.api.account.dto.AccountUserSettingsDto
import com.vk.sdk.api.photos.dto.PhotosGetAlbumsResponseDto
import com.vk.sdk.api.photos.dto.PhotosGetResponseDto
import com.vk.sdk.api.photos.dto.PhotosPhotoAlbumFullDto
import kotlinx.coroutines.flow.*
import kotlin.random.Random

data class DiceUiState(
    val numberOfRolls: String? = "nuyll",
)

class MainViewModel : ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(DiceUiState())
    val uiState: StateFlow<DiceUiState> = _uiState.asStateFlow()

    // Handle business logic
    fun rollDice() {
        VK.execute(PhotosService().photosGetAlbums(), object:
            VKApiCallback<PhotosGetAlbumsResponseDto> {
            override fun success(result: PhotosGetAlbumsResponseDto) {
                _uiState.update { currentState ->
                    currentState.copy(
                        numberOfRolls = result.items[0].title + " " + result.items[1].title
                    )
                }
            }
            override fun fail(error: Exception) {
                Log.e(TAG, error.toString())
            }
        })

        Log.i("name", _uiState.value.numberOfRolls.toString())
    }
}