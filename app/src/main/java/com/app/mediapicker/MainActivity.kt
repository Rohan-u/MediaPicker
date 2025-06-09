package com.app.mediapicker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.app.mediapicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPicker: MediaPickerImagePicker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        mediaPicker = MediaPickerImagePicker(this) { uri ->
            uri?.let {
                binding.imgSelectedImage.setImageURI(it)
            }
        }

        binding.btnSelectImage.setOnClickListener {
            mediaPicker.pickImage()
        }
    }
}