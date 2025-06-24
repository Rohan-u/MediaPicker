# 📸 MediaPicker Library

A simple and customizable Android media picker that allows users to choose an image from the **Gallery** or **Camera** using an intuitive alert-style UI.

---

## ✨ Features

- ✅ Easy Alert Dialog UI Integration
- 🖼️ Choose Image from **Gallery**
- 📷 Capture Image using **Camera**
- 🔀 Option to enable/disable **Camera** or **Gallery**

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

</details>

---

### 📋 AndroidManifest Setup

Add the following permissions and configuration:

```xml
<uses-feature android:name="android.hardware.camera" android:required="false" />
<uses-permission android:name="android.permission.CAMERA" />
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
</paths>
```

---

## 🧠 Usage

### Step 1: Use in an Activity

```kotlin
private lateinit var mediaPicker: ImagePicker

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    mediaPicker = ImagePicker(this, activityResultRegistry) { uri ->
        uri?.let {
            binding.imgSelectedImage.setImageURI(it)
        }
    }

    binding.btnSelectImage.setOnClickListener {
        mediaPicker.pickImage(this)
    }
}
```

---

### Step 2: Use in a Fragment

```kotlin
private lateinit var mediaPicker: ImagePicker

override fun onAttach(context: Context) {
    super.onAttach(context)
    mediaPicker = ImagePicker(this, requireActivity().activityResultRegistry) { uri ->
        uri?.let {
            view?.findViewById<AppCompatImageView>(R.id.imgSelectedImage)
                ?.setImageURI(it)
        }
    }
}

override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View {
    binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.btnSelectImage.setOnClickListener {
        mediaPicker.pickImage(requireContext())
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
