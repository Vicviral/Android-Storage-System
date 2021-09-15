package com.example.androidstorage.models

import android.graphics.Bitmap
import android.net.Uri

data class Shared(
    val id: Long,
    val name: String,
    val width: Int,
    val height: Int,
    val contentUri: Uri
)