package com.example.zedgeimagelist.presentation

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.zedgeimagelist.R
import com.example.zedgeimagelist.databinding.ActivityDetailsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class DetailsActivity : AppCompatActivity() {
    companion object {
        const val PARAM_IMAGE_ID = "image_id"
    }

    private val model: DetailsViewModel by viewModel(state = { Bundle(intent.extras) })

    private lateinit var binding: ActivityDetailsBinding

    private var bitmap: Bitmap? = null
    private var isFavored = false

    private val shareThrottle = ClickThrottle(lifecycleScope) {
        shareBitmap()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model.image.observe(this) {
            isFavored = it.favorite
            invalidateOptionsMenu()

            Glide.with(this)
                .asBitmap()
                .load(it.largeImageURL)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bitmap = resource
                        binding.image.setImageBitmap(resource)

                        binding.image.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.GONE
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (isFavored) {
            menu?.findItem(R.id.action_favorite)?.setIcon(R.drawable.ic_action_favorite)
        } else {
            menu?.findItem(R.id.action_favorite)?.setIcon(R.drawable.ic_action_favorite_empty)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_share -> {
            shareThrottle.click()
            true
        }
        R.id.action_favorite -> {
            model.toggleFavorite(!isFavored)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun shareBitmap() {
        bitmap?.let {
            val imageUri = getLocalBitmapUri(it)
            if (imageUri == null) {
                Toast.makeText(this, "Unable to share image", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                }
                startActivity(Intent.createChooser(intent, "Share Image"))
            }
        }
    }

    private fun getLocalBitmapUri(bitmap: Bitmap): Uri? {
        return try {
            val cachePath = File(cacheDir, "images")
            cachePath.mkdirs()

            val cacheFile = File(cachePath, "image.jpg")
            val imageStream = FileOutputStream(cacheFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, imageStream)
            imageStream.close()

            FileProvider.getUriForFile(this, "com.example.zedgeimagelist.fileprovider", cacheFile)
        } catch (e: IOException) {
            null
        }
    }
}