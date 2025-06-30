package com.app.mediapicker.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.mediapicker.dataModel.MediaFile
import com.app.mediapicker.databinding.ItemImageListBinding

class MediaAdapter(private val mediaList: List<MediaFile>) :
    RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    private var mediaListData: MutableList<MediaFile> = mediaList as MutableList<MediaFile>

    class MediaViewHolder(val binding: ItemImageListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemImageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaListData[position]

        if (media.isImage) {
            holder.binding.imgPreview.visibility = View.VISIBLE
            holder.binding.textFilename.visibility = View.GONE
            holder.binding.imgPreview.setImageURI(media.uri)
        } else {
            // Document: show file name only
            holder.binding.imgPreview.visibility = View.GONE
            holder.binding.textFilename.visibility = View.VISIBLE
            holder.binding.textFilename.text = media.fileName
        }

        holder.binding.imgClose.setOnClickListener {
            deleteItem(position)
        }
    }

    override fun getItemCount(): Int = mediaList.size

    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(position: Int) {
        mediaListData.removeAt(position)
        notifyDataSetChanged()
    }
}

