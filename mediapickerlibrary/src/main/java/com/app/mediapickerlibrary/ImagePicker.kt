package com.app.mediapickerlibrary

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner

class ImagePicker(
    owner: LifecycleOwner,
    registry: ActivityResultRegistry,
    onImagePicked: (Uri?) -> Unit
) {

    private var imageUri: Uri? = null

    var disableGallery = false // Set to true if you want to disable gallery option
    var disableCamera = false // Set to true if you want to disable camera option

    private val getContentLauncher: ActivityResultLauncher<String> =
        registry.register("getContent", owner, ActivityResultContracts.GetContent()) { uri: Uri? ->
            onImagePicked(uri)
        }

    private val takePictureLauncher: ActivityResultLauncher<Uri> =
        registry.register("takePicture", owner, ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                onImagePicked(imageUri!!)
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
