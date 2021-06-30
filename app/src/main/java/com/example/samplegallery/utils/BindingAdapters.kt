package com.example.samplegallery.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.samplegallery.R
import com.google.android.material.snackbar.Snackbar

@BindingAdapter("imageUrl")
fun setImageFromUrl(imageView: ImageView, url: String?) {
    val requestOptions = RequestOptions.placeholderOf(R.drawable.ic_image_placeholder)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .error(R.drawable.ic_broken_image)
    if (!url.isNullOrBlank())
        Glide.with(imageView.context)
            .setDefaultRequestOptions(requestOptions)
            .load(url)
            .into(imageView)
}

@BindingAdapter("error")
fun showError(view: View, error: String?) {
    if (!error.isNullOrEmpty())
        Snackbar.make(view, error, Snackbar.LENGTH_LONG).show()
}