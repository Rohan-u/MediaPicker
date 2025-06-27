package com.app.mediapicker.dataModel

import android.net.Uri

data class MediaFile(
    val uri: Uri,
    val fileName: String?,
    val isImage: Boolean // true = image, false = document
)
