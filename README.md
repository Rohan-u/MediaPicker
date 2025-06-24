📸 MediaPicker Library
A simple and customizable Android media picker that allows users to choose an image from the Gallery or Camera with an intuitive alert-style UI.

✨ Features
✅ Easy Alert Dialog UI Integration

🖼️ Choose Image from Gallery

📷 Capture Image using Camera

🔀 Option to enable/disable either Camera or Gallery

🚀 Integration
Step 1: Add the Dependency
<details> <summary>Gradle (Kotlin DSL)</summary>
kotlin
Copy
Edit
dependencies {
    implementation("com.github.Rohan-u:MediaPicker:v1.0.3")
}
</details>
📋 AndroidManifest Setup
Add the following permissions and configurations:

xml
Copy
Edit
<uses-feature android:name="android.hardware.camera" android:required="false" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />
Also, add the FileProvider setup inside your <application> tag:

xml
Copy
Edit
<provider
android:name="androidx.core.content.FileProvider"
android:authorities="${applicationId}.fileprovider"
android:exported="false"
android:grantUriPermissions="true">
<meta-data
android:name="android.support.FILE_PROVIDER_PATHS"
android:resource="@xml/file_paths" />
</provider>
📁 file_paths.xml (under res/xml/)
xml
Copy
Edit
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path name="my_images" path="Pictures/" />
</paths>
🧠 Usage
Step 1: Initialize the Picker
ImagePicker.kt
kotlin
Copy
Edit
class ImagePicker(
    owner: LifecycleOwner,
    registry: ActivityResultRegistry,
    onImagePicked: (Uri?) -> Unit
) {
    private var imageUri: Uri? = null

    var disableGallery = false
    var disableCamera = false

    private val getContentLauncher =
        registry.register("getContent", owner, ActivityResultContracts.GetContent()) { uri ->
            onImagePicked(uri)
        }

    private val takePictureLauncher =
        registry.register("takePicture", owner, ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                onImagePicked(imageUri!!)
            }
        }

    fun pickImage(context: Context) {
        MediaPicker.showDialogMediaPicker(
            context,
            getContentLauncher,
            onCameraUriPrepared = { uri ->
                imageUri = uri
                takePictureLauncher.launch(uri)
            },
            disableGallery = disableGallery,
            disableCamera = disableCamera
        )
    }
}
Step 2: Use in an Activity
kotlin
Copy
Edit
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
Step 3: Use in a Fragment
kotlin
Copy
Edit
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
🔧 Customization
You can control whether to disable Camera or Gallery options by setting:

kotlin
Copy
Edit
mediaPicker.disableCamera = true
mediaPicker.disableGallery = true