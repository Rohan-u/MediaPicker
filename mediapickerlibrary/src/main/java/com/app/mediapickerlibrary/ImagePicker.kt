package com.app.mediapickerlibrary

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class ImagePicker(
    caller: ActivityResultCaller,
    private val onImagePicked: (Uri?) -> Unit,
) {
    var disableGallery = false
    var disableCamera = false

    // Gallery picker
    private val getContentLauncher: ActivityResultLauncher<String> =
        caller.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            onImagePicked(uri)
        }

    // Document picker
    val documentPickerLauncher =
        caller.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            onImagePicked(uri)
        }

    init {
        MediaPicker.registerCameraHandlers(caller, onImagePicked)
    }

    fun pickImage(context: Context) {
        MediaPicker.showDialogMediaPicker(
            context,
            getContentLauncher,
            onCameraRequested = {
                MediaPicker.requestPermissionsAndOpenCamera(context)
            },
            disableGallery = disableGallery,
            disableCamera = disableCamera,
            getDocumentLauncher = documentPickerLauncher
        )
    }
}
