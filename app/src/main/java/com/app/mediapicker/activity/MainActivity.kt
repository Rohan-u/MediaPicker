package com.app.mediapicker.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.app.mediapicker.databinding.ActivityMainBinding
import com.app.mediapicker.fragment.HomeFragment
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
            mediaPicker.pickImage(this@MainActivity)
        }

        binding.btnFragment.setOnClickListener {
            binding.fragmentContainer.visibility = View.VISIBLE
            supportFragmentManager.beginTransaction()
                .replace(com.app.mediapicker.R.id.fragment_container, HomeFragment())
                .commit()
        }
    }
}