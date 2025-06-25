package com.app.mediapickerlibrary

import android.Manifest
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
        disableCamera: Boolean
    ) {
        if (disableGallery && disableCamera) {
            // If both are disabled, do nothing or show a message
            Toast.makeText(context, "Enable camera or gallery", Toast.LENGTH_SHORT).show()
            return
        }
        if (disableGallery || disableCamera) {
            if (disableGallery){
                openCamera(context, onCameraUriPrepared)
            } else if (disableCamera) {
                getContentLauncher.launch("image/*")
            }
        }else {
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
            camera.setOnClickListener { v: View? ->
                popUpDialog.dismiss()
                openCamera(context, onCameraUriPrepared)
            }

            gallery.setOnClickListener { v: View? ->
                popUpDialog.dismiss()
                getContentLauncher.launch("image/*")
            }
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
    private fun checkPermissions(context: Context): Boolean {
        val permissions: ArrayList<String> = ArrayList(listOf(Manifest.permission.CAMERA))
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
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