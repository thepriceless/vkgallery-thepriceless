package com.example.vkgallery.activities

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vkgallery.R
import com.example.vkgallery.extra.FileTransferedJsonResponse
import com.example.vkgallery.extra.InputStreamRequestBody
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.sdk.api.photos.PhotosService
import com.vk.sdk.api.photos.dto.PhotosPhotoDto
import com.vk.sdk.api.photos.dto.PhotosPhotoUploadDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class SelectingPhotosActivity : AppCompatActivity() {
    private val GALLERY_PERMISSION_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selecting_photos)

        checkPermission(Manifest.permission.READ_MEDIA_IMAGES, GALLERY_PERMISSION_CODE)

        val pickMultipleMedia = registerForActivityResult(PickMultipleVisualMedia(10)) { uris ->
            if (uris.isNotEmpty()) {
                Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
                VK.execute(PhotosService().photosGetUploadServer(
                    albumId = intent.getIntExtra("album_id", 0)),
                    object: VKApiCallback<PhotosPhotoUploadDto> {
                        override fun success(result: PhotosPhotoUploadDto) {
                            val client = OkHttpClient()
                            val photo = InputStreamRequestBody(MultipartBody.FORM, contentResolver, uris[0])
                            val requestBody = MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("file1", "file1.jpg", photo)
                                .build()

                            Log.d("res", requestBody.parts[0].body.toString())

                            val request = Request.Builder()
                                .url(result.uploadUrl)
                                .post(requestBody)
                                .build()

                            val scope = CoroutineScope(Dispatchers.IO)

                            scope.launch {
                                val response = client.newCall(request).execute()

                                withContext(Dispatchers.Main) {
                                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                                    val data = Json.decodeFromString<FileTransferedJsonResponse>(response.body?.string().toString())
                                    Log.i("photos", data.toString())

                                    VK.execute(PhotosService().photosSave(
                                        albumId = data.aid,
                                        server = data.server,
                                        photosList = data.photos_list,
                                        hash = data.hash
                                    ), object: VKApiCallback<List<PhotosPhotoDto>> {
                                        override fun success(result: List<PhotosPhotoDto>) {
                                            Log.i("Success", result[0].photo256.toString())
                                        }

                                        override fun fail(error: Exception) {
                                            Log.e(ContentValues.TAG, error.toString())
                                        }
                                    })
                                }
                            }
                        }
                        override fun fail(error: Exception) {
                            Log.e(ContentValues.TAG, error.toString())
                        }
                })
            }
            else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        pickMultipleMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageAndVideo))
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == GALLERY_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}