package com.app.mediapicker.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.app.mediapicker.adapter.MediaAdapter
import com.app.mediapicker.dataModel.MediaFile
import com.app.mediapicker.databinding.FragmentHomeBinding
import com.app.mediapickerlibrary.ImagePicker

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mediaPicker: ImagePicker
    private lateinit var mediaAdapter: MediaAdapter
    private val mediaFileList = mutableListOf<MediaFile>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMediaPicker()
        initRecyclerView()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSelectImage.setOnClickListener { mediaPicker.pickImage(requireActivity()) }
    }

    private fun initRecyclerView() {
        mediaAdapter = MediaAdapter(mediaFileList)
        binding.recyclerViewImages.adapter = mediaAdapter

        binding.recyclerViewImages.visibility = View.GONE
        binding.imgSelectedImage.visibility = View.GONE
        binding.videoView.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupMediaPicker() {
        val supportedDocs = setOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )

        mediaPicker = ImagePicker(
            this,
            maxImages = 5,
            onImagePicked = { uriList ->
                uriList?.takeIf { it.isNotEmpty() }?.let {
                    mediaFileList.clear()

                    val newMedia = uriList.mapNotNull { uri ->
                        val type =
                            requireActivity().contentResolver.getType(uri) ?: return@mapNotNull null

                        when {
                            type.startsWith("image/") -> {
                                updateMediaVisibility("List")
                                MediaFile(uri, null, isImage = true)
                            }

                            type in supportedDocs -> {
                                updateMediaVisibility("List")
                                MediaFile(uri, getFileName(uri), isImage = false)
                            }

                            else -> null
                        }
                    }

                    mediaFileList.addAll(newMedia)
                    mediaAdapter.notifyDataSetChanged()


                    // Show RecyclerView only if list is not empty and video is not full-screen
                    binding.recyclerViewImages.visibility =
                        if (mediaFileList.isNotEmpty()) View.VISIBLE else View.GONE

                    // Optional: Hide single preview views if not needed
                    binding.imgSelectedImage.visibility = View.GONE
                    // Note: Don't hide videoView here if you want to show the video immediately
                }
            },
            onCameraOrImageOrVideoUriPrepared = { uri ->
                uri?.let {
                    val type = requireActivity().contentResolver.getType(it)
                    when {
                        type!!.startsWith("video/") -> {
                            updateMediaVisibility("Video")
                            showVideo(uri) // This should return a MediaFile
                        }

                        type.startsWith("image/") -> {
                            updateMediaVisibility("Camera")
                            binding.imgSelectedImage.setImageURI(uri)
                        }
                    }
                }
            })
    }

    private fun getFileName(uri: Uri): String? =
        requireActivity().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index != -1) cursor.getString(index) else null
        }

    private fun showVideo(uri: Uri): MediaFile {
        with(binding) {
            imgSelectedImage.visibility = View.GONE
            recyclerViewImages.visibility = View.GONE
            videoView.visibility = View.VISIBLE

            val controller = MediaController(requireContext()).apply {
                setAnchorView(videoView)
            }

            videoView.apply {
                setMediaController(controller)
                setVideoURI(uri)
                setOnPreparedListener {
                    it.isLooping = true
                    start()
                }
            }
        }
        return MediaFile(uri, getFileName(uri), isImage = false)
    }

    private fun updateMediaVisibility(type: String) {
        with(binding) {
            imgSelectedImage.visibility = if (type == "Camera") View.VISIBLE else View.GONE
            videoView.visibility = if (type == "Video") View.VISIBLE else View.GONE
            recyclerViewImages.visibility = if (type == "List") View.VISIBLE else View.GONE
        }
    }

}
