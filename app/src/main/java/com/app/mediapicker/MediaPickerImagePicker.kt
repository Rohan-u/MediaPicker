package com.app.mediapicker

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MediaPickerImagePicker(
    private val activity: ComponentActivity,
    private val onImagePicked: (Uri?) -> Unit
) {

    private val getContentLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            onImagePicked(uri)
        }

    fun pickImage() {
        getContentLauncher.launch("image/*")
    }
}
