package com.app.mediapickerlibrary

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class ImagePicker(
    activity: ComponentActivity,
    onImagePicked: (Uri?) -> Unit
) {

    private val getContentLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            onImagePicked(uri)
        }

    fun pickImage() {
        getContentLauncher.launch("image/*")
    }
}
