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
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        enableEdgeToEdge()

        setupMediaPicker()
        setupClickListeners()
    }

    private fun setupMediaPicker() {
        mediaPicker = ImagePicker(this) { uri ->
            uri?.let {
                val mimeType = contentResolver.getType(it)
                when {
                    mimeType?.startsWith("image/") == true -> binding.imgSelectedImage.setImageURI(
                        it
                    )

                    else -> handleDocument(it, mimeType)
                }
            }
        }
    }

    private fun setupClickListeners() = with(binding) {
        btnSelectImage.setOnClickListener { mediaPicker.pickImage(this@MainActivity) }
        btnFragment.setOnClickListener {
            fragmentContainer.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .replace(com.app.mediapicker.R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    private fun handleDocument(uri: Uri, mimeType: String?) {
        val allowedTypes = setOf(
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/csv"
        )

        if (mimeType !in allowedTypes) {
            showToast("Unsupported file type: $mimeType")
            return
        }

        binding.textFilename.text = "fileName: ${getFileName(uri)}"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            startActivity(Intent.createChooser(intent, "Open with"))
        } catch (e: ActivityNotFoundException) {
            showToast("No app found to open this document: ${e.message}")
        }
    }

    private fun getFileName(uri: Uri): String? =
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index != -1) cursor.getString(index) else null
        }

    private fun showToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
