# 📸 MediaPicker Library

A simple and customizable Android media picker that allows users to choose an image from the **Gallery** or **Camera** using an intuitive alert-style UI.

---

## ✨ Features

✨ Features

✅ Easy Alert Dialog UI Integration
🖼️ Choose Image from Gallery
📷 Capture Image using Camera
🔀 Option to enable/disable Camera or Gallery
📄 Allows users to upload documents and open them with Google Drive or other compatible applications

📸 New Features

🖼️ Allows selecting up to 5 images using the PickVisualMedia API
🎥 Handles camera or video input through the onCameraOrImageOrVideoUriPrepared callback
🔢 The ImagePicker constructor accepts a maxItems parameter to limit the number of selectable images

---

## 🚀 Integration

### Step 1: Add the Dependency

<details>
<summary>Gradle (Kotlin DSL)</summary>

```kotlin
dependencies {
    implementation("com.github.Rohan-u:MediaPicker:v1.0.3")
}
```
### Step 2: Add the setting.gradle
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
```
</details>

---

### 📋 AndroidManifest Setup

Add the following permissions and configuration:

```xml
<uses-feature android:name="android.hardware.camera" android:required="false" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
```

Add the `FileProvider` inside your `<application>` tag:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

---

### 📁 `file_paths.xml` (under `res/xml/`)

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path name="my_images" path="Pictures/" />

    <!-- document external files-->
    <external-path
        name="external_files"
        path="." />
</paths>

```

---

## 🧠 Usage

### Step 1: Use in an Activity

```kotlin
private lateinit var mediaPicker: ImagePicker

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
 
```

---

### Step 2: Use in a Fragment

```kotlin
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

                    if (newMedia.first().isImage) {
                        binding.recyclerViewImages.layoutManager =
                            GridLayoutManager(requireActivity(), 4)
                    } else {
                        binding.recyclerViewImages.layoutManager = GridLayoutManager(requireActivity(), 2)
                    }

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
    
```

---

## 🔧 Customization

You can enable or disable Gallery or Camera options easily:

```kotlin
mediaPicker.disableCamera = true
mediaPicker.disableGallery = true
```

Set max number of selectable Image items:
```kotlin
mediaPicker = ImagePicker(this, maxItems = 5) { uriList ->
    // Handle multiple URIs
}

```
---

## 📜 License

This project is licensed under the MIT License.

---

## 🤝 Contributions

Contributions, issues, and feature requests are welcome! Feel free to open an issue or submit a pull request.

---

## 💬 Contact

Maintained by [Rohan-u](https://github.com/Rohan-u) — feel free to reach out for feedback or collaboration!
