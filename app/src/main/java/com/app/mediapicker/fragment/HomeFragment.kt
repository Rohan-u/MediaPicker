package com.app.mediapicker.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                binding.imgSelectedImage.setImageURI(it)
            }
        }

        binding.btnSelectImage.setOnClickListener {
            mediaPicker.pickImage(requireContext())
        }
    }
}
