# 📸 MediaPicker Library

A simple and customizable Android media picker that allows users to choose an image from the **Gallery** or **Camera** using an intuitive alert-style UI.

---

## ✨ Features

- ✅ Easy Alert Dialog UI Integration
- 🖼️ Choose Image from **Gallery**
- 📷 Capture Image using **Camera**
- 🔀 Option to enable/disable **Camera** or **Gallery**
- 📄 Allows users to upload documents and open them with Google Drive or other compatible applications

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
                    handleDocumentOpen(uri, mimeType) // Handle document types
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

    // Handle the result from the document picker
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

    // Function to get the file name from the URI
    private fun getFileNameFromUri(uri: Uri): String? {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) cursor.getString(nameIndex) else null
        }
    }
 
```

---

### Step 2: Use in a Fragment

```kotlin
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
                val mimeType = requireActivity().contentResolver.getType(it) // Get the MIME type of the selected file

                if (mimeType?.startsWith("image/") == true) {
                    binding.imgSelectedImage.setImageURI(it) // Display the selected image
                } else {
                    handleDocumentOpen(uri, mimeType) // Handle document types
                }
            }
        }

        binding.btnSelectImage.setOnClickListener {
            mediaPicker.pickImage(requireContext())
        }
    }

    // Handle the result from the document picker
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

    // Function to get the file name from the URI
    private fun getFileNameFromUri(uri: Uri): String? {
        return requireActivity().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) cursor.getString(nameIndex) else null
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

---

## 📜 License

This project is licensed under the MIT License.

---

## 🤝 Contributions

Contributions, issues, and feature requests are welcome! Feel free to open an issue or submit a pull request.

---

## 💬 Contact

Maintained by [Rohan-u](https://github.com/Rohan-u) — feel free to reach out for feedback or collaboration!
