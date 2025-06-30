package com.app.mediapicker.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.MediaController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.app.mediapicker.adapter.MediaAdapter
import com.app.mediapicker.dataModel.MediaFile
import com.app.mediapicker.databinding.ActivityMainBinding
import com.app.mediapicker.fragment.HomeFragment
import com.app.mediapickerlibrary.ImagePicker

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPicker: ImagePicker
    private val mediaFileList = mutableListOf<MediaFile>()
    private lateinit var mediaAdapter: MediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        enableEdgeToEdge()

        setupMediaPicker()
        setupClickListeners()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mediaAdapter = MediaAdapter(mediaFileList)
        binding.recyclerViewImages.layoutManager = GridLayoutManager(this, 4)
        binding.recyclerViewImages.adapter = mediaAdapter
    }

    // Initialize the ImagePicker to allow picking up to 5 images or supported documents,
    // and handle camera or video input through onCameraOrImageOrVideoUriPrepared
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
            maxImages = 5, // maxItems parameter to limit the number of selectable images
            onImagePicked = { uriList ->
                uriList?.takeIf { it.isNotEmpty() }?.let {
                    mediaFileList.clear()
                    val newMedia = uriList.mapNotNull { uri ->
                        val type = contentResolver.getType(uri) ?: return@mapNotNull null

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

                    binding.recyclerViewImages.visibility =
                        if (mediaFileList.isNotEmpty()) View.VISIBLE else View.GONE
                }
            },
            onCameraOrImageOrVideoUriPrepared = { uri ->
                uri?.let {
                    val type = contentResolver.getType(it)
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
            }
        )
    }

    private fun showVideo(uri: Uri): MediaFile {
        with(binding) {
            val controller = MediaController(this@MainActivity).apply {
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

    private fun setupClickListeners() {
        binding.btnSelectImage.setOnClickListener { mediaPicker.pickImage(this@MainActivity) }
        binding.btnFragment.setOnClickListener {
            binding.fragmentContainer.visibility = View.VISIBLE
            binding.videoView.visibility = View.GONE
            binding.imgSelectedImage.visibility = View.GONE
            binding.recyclerViewImages.visibility = View.GONE

            supportFragmentManager.beginTransaction()
                .replace(com.app.mediapicker.R.id.fragment_container, HomeFragment()).commit()
        }
    }

    private fun getFileName(uri: Uri): String? =
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index != -1) cursor.getString(index) else null
        }

    private fun updateMediaVisibility(type: String) {
        with(binding) {
            imgSelectedImage.visibility = if (type == "Camera") View.VISIBLE else View.GONE
            videoView.visibility = if (type == "Video") View.VISIBLE else View.GONE
            recyclerViewImages.visibility = if (type == "List") View.VISIBLE else View.GONE
        }
    }
}
