package com.example.vkgallery.extra

import kotlinx.serialization.Serializable

@Serializable
data class FileTransferedJsonResponse(
    val server: Int,
    val photos_list: String,
    val aid: Int,
    val hash: String
)
