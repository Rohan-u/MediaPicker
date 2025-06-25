package com.app.mediapickerlibrary

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class ImagePicker(
    caller: ActivityResultCaller,
    private val onImagePicked: (Uri?) -> Unit
) {

    private var imageUri: Uri? = null

    var disableGallery = false
    var disableCamera = false

    private val getContentLauncher: ActivityResultLauncher<String> =
        caller.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            onImagePicked(uri)
        }

    private val takePictureLauncher: ActivityResultLauncher<Uri> =
        caller.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                onImagePicked(imageUri)
            }
        }

    fun pickImage(context: Context) {
        MediaPicker.showDialogMediaPicker(
            context,
            getContentLauncher,
            onCameraUriPrepared = { uri ->
                imageUri = uri
                takePictureLauncher.launch(uri)
            },
            disableGallery = disableGallery,
            disableCamera = disableCamera
        )
    }
}
