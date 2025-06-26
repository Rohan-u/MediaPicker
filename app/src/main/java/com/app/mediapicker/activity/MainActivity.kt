package com.app.mediapicker.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.app.mediapicker.databinding.ActivityMainBinding
import com.app.mediapicker.fragment.HomeFragment
import com.app.mediapickerlibrary.ImagePicker

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPicker: ImagePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        mediaPicker = ImagePicker(this) { uri ->
            uri?.let {
                val mimeType = contentResolver.getType(it)

                if (mimeType?.startsWith("image/") == true) {
                    binding.imgSelectedImage.setImageURI(it)
                } else {
                    handleDocumentOpen(uri, mimeType)
                }
            }
        }

        binding.btnSelectImage.setOnClickListener {
            mediaPicker.pickImage(this@MainActivity)
        }

        binding.btnFragment.setOnClickListener {
            binding.fragmentContainer.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .replace(com.app.mediapicker.R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    private fun handleDocumentOpen(uri: Uri, mimeType: String?) {
        val allowedTypes = setOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/csv"
        )

        if (mimeType !in allowedTypes) {
            Toast.makeText(this, "Unsupported file type: $mimeType", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the file name from the URI
        val fileName = getFileNameFromUri(uri)
        binding.textFilename.text = "fileName: $fileName"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            startActivity(Intent.createChooser(intent, "Open with"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No app found to open this document :: ${e.message.toString()}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) cursor.getString(nameIndex) else null
        }
    }
}