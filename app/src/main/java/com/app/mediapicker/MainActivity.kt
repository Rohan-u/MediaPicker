package com.app.mediapicker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.app.mediapicker.databinding.ActivityMainBinding
import com.app.mediapickerlibrary.ImagePicker

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPicker: ImagePicker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        mediaPicker = ImagePicker(this) { uri ->
            uri?.let {
                binding.imgSelectedImage.setImageURI(it)
            }
        }

        binding.btnSelectImage.setOnClickListener {
            Toast.makeText(this, "Hello button clicked", Toast.LENGTH_LONG).show()
            mediaPicker.pickImage()
        }
    }
}