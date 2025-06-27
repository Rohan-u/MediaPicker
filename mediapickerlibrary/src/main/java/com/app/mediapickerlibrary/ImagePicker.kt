package com.app.mediapickerlibrary

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class ImagePicker(
    caller: ActivityResultCaller,
    private val onImagePicked: (List<Uri>?) -> Unit,
    private val onCameraUriPrepared: (Uri?) -> Unit
) {
    private var imageUri: Uri? = null
    var disableGallery = false
    var disableCamera = false
    var disableDocument = false
    var disableVideo = false

    // Register the activity result launcher for getting content (images)
    private val getContentLauncher: ActivityResultLauncher<String> =
        caller.registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
            onImagePicked(uriList)
        }

    // Launcher for taking a picture using the camera
    private val takePictureLauncher: ActivityResultLauncher<Uri> =
        caller.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                onCameraUriPrepared(imageUri!!)
            }
        }

    // launcher for multiple document picking
    val documentPickerLauncher: ActivityResultLauncher<Array<String>> =
        caller.registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uriList ->
            onImagePicked(uriList)
        }

    // launcher for video picking
    val videoPickerLauncher: ActivityResultLauncher<String> =
        caller.registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
            onImagePicked(uriList)
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
            disableCamera = disableCamera,
            disableDocument = disableDocument,
            disableVideo = disableVideo,
            getDocumentLauncher = documentPickerLauncher,
            videoPickerLauncher = videoPickerLauncher
        )
    }
}
