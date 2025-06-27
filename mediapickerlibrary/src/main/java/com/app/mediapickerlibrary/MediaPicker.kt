package com.app.mediapickerlibrary

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toDrawable
import java.io.File

object MediaPicker {
    fun showDialogMediaPicker(
        context: Context,
        getContentLauncher: ActivityResultLauncher<String>,
        onCameraUriPrepared: (Uri) -> Unit,
        disableGallery: Boolean,
        disableCamera: Boolean,
        disableDocument : Boolean,
        disableVideo : Boolean,
        getDocumentLauncher: ActivityResultLauncher<Array<String>>,
        videoPickerLauncher: ActivityResultLauncher<String>,
    ) {
        if (disableGallery && disableCamera && disableDocument && disableVideo) {
            Toast.makeText(context, "All options are disabled. Please enable at least one.", Toast.LENGTH_SHORT).show()
            return
        }
        if (disableGallery || disableCamera) {
            if (disableGallery) {
                openCamera(context, onCameraUriPrepared)
            } else if (disableCamera) {
                getContentLauncher.launch("image/*")
            }
        } else {
            val popUpDialog = Dialog(context)
            popUpDialog.setContentView(R.layout.layout_media_picker)
            popUpDialog.window!!.setDimAmount(0.5f)
            popUpDialog.window!!.setWindowAnimations(R.style.CustomDialogStyle)
            popUpDialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popUpDialog.window!!.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            popUpDialog.window!!.setGravity(Gravity.BOTTOM)
            popUpDialog.show()
            val camera = popUpDialog.findViewById<TextView>(R.id.camera)
            val gallery = popUpDialog.findViewById<TextView>(R.id.gallery)
            val document = popUpDialog.findViewById<TextView>(R.id.document)
            val video = popUpDialog.findViewById<TextView>(R.id.video)
            camera.setOnClickListener { v: View? ->
                popUpDialog.dismiss()
                openCamera(context, onCameraUriPrepared)
            }
            gallery.setOnClickListener { v: View? ->
                popUpDialog.dismiss()
                getContentLauncher.launch("image/*")
            }
            document.setOnClickListener { v: View? ->
                popUpDialog.dismiss()
                openDocumentPicker(context, getDocumentLauncher)
            }
            video.setOnClickListener { v: View? ->
                popUpDialog.dismiss()
                openVideoPicker(context, videoPickerLauncher)
            }
        }
    }

    private fun openVideoPicker(
        context: Context,
        videoPickerLauncher: ActivityResultLauncher<String>
    ) {
        if (checkPermissions(context)) {
            videoPickerLauncher.launch("video/*")
        }
    }

    private fun openDocumentPicker(
        context: Context,
        documentPickerLauncher: ActivityResultLauncher<Array<String>>
    ) {
        if (checkPermissions(context)) {
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

    private fun openCamera(
        context: Context,
        onCameraUriPrepared: (Uri) -> Unit
    ) {
        if (checkPermissions(context)) {

            val photoFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "captured_image_${System.currentTimeMillis()}.jpg"
            )
            val imageUri = FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", photoFile
            )
            onCameraUriPrepared(imageUri)
        }
    }

    // check permission
    @SuppressLint("ObsoleteSdkInt")
    private fun checkPermissions(context: Context): Boolean {
        val permissions: ArrayList<String> = ArrayList(listOf(Manifest.permission.CAMERA))
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(Manifest.permission.READ_MEDIA_VIDEO)
            }
        }
        for (p in ArrayList<String>(permissions)) {
            result = ContextCompat.checkSelfPermission(context, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                context as Activity, listPermissionsNeeded.toTypedArray<String>(), 1001
            )
            return false
        }
        return true
    }
}
