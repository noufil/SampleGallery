package com.example.samplegallery.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.*
import androidx.recyclerview.widget.RecyclerView
import com.example.samplegallery.R
import com.example.samplegallery.base.BaseViewHolder
import com.example.samplegallery.databinding.ItemImageBinding
import com.example.samplegallery.databinding.ItemLoadingBinding
import com.example.samplegallery.databinding.ItemViewPagerImageBinding
import com.example.samplegallery.home.models.ImageInfo
import com.example.samplegallery.home.models.ImageSearchResponse

private const val TYPE_LOADING = 0
private const val TYPE_IMAGE = 1
private const val TYPE_VIEW_PAGER_IMAGE = 2

class ImageListAdapter(
    private val pageRequest: PageRequestListener,
    private val clickListener: View.OnClickListener?,
) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var totalImages = 0
    private var imageInfoList: MutableList<ImageInfo> = mutableListOf()
    private var recyclerView: RecyclerView? = null
    private var currentSize = 0
    private var isLoading: Boolean? = false
    var columns: Int = 2
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder(when (viewType) {
            TYPE_IMAGE ->
                ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    .apply { clickListener = this@ImageListAdapter.clickListener }
            TYPE_VIEW_PAGER_IMAGE ->
                ItemViewPagerImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            else -> ItemLoadingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        })

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_IMAGE -> (holder.binding as ItemImageBinding).apply {
                imageInfo = imageInfoList[position]
                image.setTag(R.id.index, position)
                transitionName = "imageView$position"
                executePendingBindings()
            }
            TYPE_VIEW_PAGER_IMAGE -> (holder.binding as ItemViewPagerImageBinding).apply {
                imageInfo = imageInfoList[position]
                transitionName = "imageView$position"
                executePendingBindings()
            }
            TYPE_LOADING -> pageRequest.nextPage()
        }
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        when (holder.itemViewType) {
            TYPE_IMAGE -> (holder.binding as ItemImageBinding).apply {
                image.layoutParams.height = recyclerView!!.width / columns
            }
            TYPE_LOADING -> if (clickListener == null) {
                (holder.binding as ItemLoadingBinding).loading.layoutParams.height = MATCH_PARENT
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentSize == position)
            TYPE_LOADING
        else {
            if (clickListener == null)
                TYPE_VIEW_PAGER_IMAGE
            else
                TYPE_IMAGE
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading == true)
            currentSize + 1
        else
            currentSize
    }

    fun updateData(imageSearchResponse: ImageSearchResponse) {
        totalImages = imageSearchResponse.total
        if (totalImages == 0) {
            currentSize = 0
            imageInfoList.clear()
            isLoading = false
            notifyDataSetChanged()
        } else {
            imageInfoList = imageSearchResponse.results!!
            val newSize = imageInfoList.size
            isLoading = newSize < totalImages
            notifyDataSetChanged()
            currentSize = newSize
        }
    }

    fun reset() {
        isLoading = true
        totalImages = 0
        currentSize = 0
        imageInfoList.clear()
        notifyDataSetChanged()
    }
}

interface PageRequestListener {
    fun nextPage()
}