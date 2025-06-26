package com.app.mediapickerlibrary

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toDrawable
import java.io.File

object MediaPicker {
    private var pendingCameraUri: Uri? = null
    private var cameraResultLauncher: ActivityResultLauncher<Uri>? = null
    private var permissionResultLauncher: ActivityResultLauncher<Array<String>>? = null
    private var onCameraImageCaptured: ((Uri?) -> Unit)? = null
    fun registerCameraHandlers(
        caller: ActivityResultCaller,
        onImagePicked: (Uri?) -> Unit
    ) {
        onCameraImageCaptured = onImagePicked

        cameraResultLauncher =
            caller.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    onCameraImageCaptured?.invoke(pendingCameraUri)
                } else {
                    onCameraImageCaptured?.invoke(null)
                }
            }

        permissionResultLauncher =
            caller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val allGranted = permissions.all { it.value }
                if (allGranted && pendingCameraUri != null) {
                    cameraResultLauncher?.launch(pendingCameraUri!!)
                } else {
                    onCameraImageCaptured?.invoke(null)
                }
            }
    }

    fun requestPermissionsAndOpenCamera(context: Context) {
        val permissions = mutableListOf(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissions += listOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        val photoFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "captured_image_${System.currentTimeMillis()}.jpg"
        )
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        pendingCameraUri = uri

        permissionResultLauncher?.launch(permissions.toTypedArray())
    }

    fun showDialogMediaPicker(
        context: Context,
        getContentLauncher: ActivityResultLauncher<String>,
        onCameraRequested: () -> Unit,
        disableGallery: Boolean,
        disableCamera: Boolean,
        getDocumentLauncher: ActivityResultLauncher<Array<String>>,
    ) {
        if (disableGallery && disableCamera) {
            Toast.makeText(context, "Enable camera or gallery", Toast.LENGTH_SHORT).show()
            return
        }

        if (disableGallery || disableCamera) {
            if (disableGallery) {
                onCameraRequested()
            } else {
                getContentLauncher.launch("image/*")
            }
        } else {
            val popUpDialog = Dialog(context)
            popUpDialog.setContentView(R.layout.layout_media_picker)
            popUpDialog.window!!.setDimAmount(0.5f)
            popUpDialog.window!!.setWindowAnimations(R.style.CustomDialogStyle)
            popUpDialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popUpDialog.window!!.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            popUpDialog.window!!.setGravity(Gravity.BOTTOM)
            popUpDialog.show()

            val camera = popUpDialog.findViewById<TextView>(R.id.camera)
            val gallery = popUpDialog.findViewById<TextView>(R.id.gallery)
            val document = popUpDialog.findViewById<TextView>(R.id.document)

            camera.setOnClickListener {
                popUpDialog.dismiss()
                onCameraRequested()
            }

            gallery.setOnClickListener {
                popUpDialog.dismiss()
                getContentLauncher.launch("image/*")
            }

            document.setOnClickListener {
                popUpDialog.dismiss()
                openDocumentPicker(getDocumentLauncher)
            }
        }
    }

    private fun openDocumentPicker(
        documentPickerLauncher: ActivityResultLauncher<Array<String>>
    ) {
        val mimeTypes = arrayOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/csv"
        )
        documentPickerLauncher.launch(mimeTypes)
    }
}
