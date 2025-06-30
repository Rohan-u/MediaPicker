package com.app.mediapickerlibrary

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class ImagePicker(
    caller: ActivityResultCaller,
    maxImages : Int,
    private val onImagePicked: (List<Uri>?) -> Unit,
    private val onCameraOrImageOrVideoUriPrepared: (Uri?) -> Unit
) {
    private var imageUri: Uri? = null
    var disableGallery = false
    var disableCamera = false
    var disableDocument = false
    var disableVideo = false

    // Register the ImagePicker to allow selecting multiple visual media (images and supported documents)
    private var getContentLauncher =
        caller.registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxImages)) { uriList ->
           onImagePicked(uriList)
        }

    // Launcher for taking a picture using the camera
    private val takePictureLauncher: ActivityResultLauncher<Uri> =
        caller.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                onCameraOrImageOrVideoUriPrepared(imageUri!!)
            }
        }

    // launcher for multiple document picking
    val documentPickerLauncher: ActivityResultLauncher<Array<String>> =
        caller.registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uriList ->
            onImagePicked(uriList)
        }

    // launcher for video picking
    val videoPickerLauncher: ActivityResultLauncher<String> =
        caller.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            onCameraOrImageOrVideoUriPrepared(uri)
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
