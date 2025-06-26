package com.app.mediapicker.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.mediapicker.databinding.FragmentHomeBinding
import com.app.mediapickerlibrary.ImagePicker

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mediaPicker: ImagePicker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPicker = ImagePicker(this) { uri ->
            uri?.let {
                val mimeType = requireActivity().contentResolver.getType(it)

                if (mimeType?.startsWith("image/") == true) {
                    binding.imgSelectedImage.setImageURI(it)
                } else {
                    handleDocumentOpen(uri, mimeType)
                }
            }
        }

        binding.btnSelectImage.setOnClickListener {
            mediaPicker.pickImage(requireContext())
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
            Toast.makeText(requireActivity(),"Unsupported file type: $mimeType", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(requireActivity(), "No app found to open this document :: ${e.message.toString()}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        return requireActivity().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) cursor.getString(nameIndex) else null
        }
    }
}
