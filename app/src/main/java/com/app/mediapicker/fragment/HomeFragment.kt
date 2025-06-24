package com.app.mediapicker.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.app.mediapicker.databinding.FragmentHomeBinding
import com.app.mediapickerlibrary.ImagePicker

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var mediaPicker: ImagePicker

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Register early, before lifecycle is started
        mediaPicker = ImagePicker(this, requireActivity().activityResultRegistry) { uri ->
            uri?.let {
                // Wait until view is ready before setting image
                view?.findViewById<AppCompatImageView>(com.app.mediapicker.R.id.imgSelectedImage)
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

        // Now it's safe to use mediaPicker
        binding.btnSelectImage.setOnClickListener {
            mediaPicker.pickImage(requireContext())
        }
    }
}
