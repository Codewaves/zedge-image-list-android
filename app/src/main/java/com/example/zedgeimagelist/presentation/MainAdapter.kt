package com.example.zedgeimagelist.presentation

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.zedgeimagelist.databinding.ItemImageBinding
import com.example.zedgeimagelist.databinding.ItemStatusBinding
import com.example.zedgeimagelist.model.Image
import com.example.zedgeimagelist.presentation.DetailsActivity.Companion.PARAM_IMAGE_ID

class MainAdapter(private val images: ArrayList<Image>, private val retry: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class Type { Image, Status}
    enum class Status { None, Loading, Error }

    private var status = Status.None

    class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image) {
            Glide.with(binding.image.context)
                .load(image.previewURL)
                .transition(withCrossFade())
                .into(binding.image)

            binding.favored.visibility = if (image.favorite) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailsActivity::class.java).apply {
                    putExtra(PARAM_IMAGE_ID, image.uid)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    class StatusViewHolder(private val binding: ItemStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(status: Status, retry: () -> Unit) {

            binding.retry.visibility = if (status == Status.Error) View.VISIBLE else View.GONE
            binding.progress.visibility = if (status == Status.Loading) View.VISIBLE else View.GONE

            binding.retry.setOnClickListener {
                retry()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        if (viewType == Type.Image.ordinal) {
            val binding = ItemImageBinding.inflate(layoutInflater, parent, false)
            return ImageViewHolder(binding)
        }

        val binding = ItemStatusBinding.inflate(layoutInflater, parent, false)
        return StatusViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position >= images.size) Type.Status.ordinal else Type.Image.ordinal
    }

    override fun getItemCount(): Int = images.size + if (status != Status.None) 1 else 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < images.size) {
            (holder as ImageViewHolder).bind(images[position])
        } else {
            (holder as StatusViewHolder).bind(status, retry)
        }
    }

    fun setImages(images: List<Image>, status: Status = Status.None) {
        this.images.apply {
            clear()
            addAll(images)
        }
        this.status = status
        notifyDataSetChanged()
    }
}