package com.app.mediapicker.fragment

import android.annotation.SuppressLint
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
                    handleDocument(uri, mimeType)
                }
            }
        }

        binding.btnSelectImage.setOnClickListener {
            mediaPicker.pickImage(requireContext())
        }
    }

    @SuppressLint("SetTextI18n")
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

        runCatching {
            startActivity(Intent.createChooser(intent, "Open with"))
        }.onFailure {
            showToast("No app found to open this document: ${it.message}")
        }
    }

    private fun getFileName(uri: Uri): String? =
        requireContext().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.takeIf { it.moveToFirst() }?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                ?.takeIf { it != -1 }?.let(cursor::getString)
        }

    private fun showToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
