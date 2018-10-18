package com.interview.itaycohen.assignmentyit

import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.v4.widget.ContentLoadingProgressBar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.NO_ID
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide

interface PhotosAdapterInterface {
    fun onReachedToBottom()
}

class PhotosAdapter(var images : ArrayList<ImageData>) : RecyclerView.Adapter<PhotosAdapter.BaseViewHolder>() {

    var shouldShowProgressBar: Boolean = true

    companion object {
        private const val FOOTER = 10
        private const val REGULAR = 20
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == (images.size)) FOOTER else REGULAR
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder : BaseViewHolder
        var itemView : View?
        when (viewType) {
            REGULAR -> {
                itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_layout, parent, false)
                holder = RegularViewHolder (itemView)
            }
            FOOTER -> {
                itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_progress_item, parent, false)
                holder = FooterViewHolder(itemView)
            }
            else ->
                holder = BaseViewHolder(null)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return images.size + 1 // +1 : footer is added
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is RegularViewHolder ->
                Glide
                        .with(holder.itemView.context)
                        .load(images.get(position).url)
                        .into(holder.imageView)
            is FooterViewHolder ->
                if (shouldShowProgressBar) holder.progressBar.show() else holder.progressBar.hide()
        }
    }

    open class BaseViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    class RegularViewHolder(itemView: View?) : BaseViewHolder(itemView) {
        val imageView : ImageView = itemView as ImageView
    }

    class FooterViewHolder(itemView: View?) : BaseViewHolder(itemView) {
        val progressBar : ContentLoadingProgressBar = itemView as ContentLoadingProgressBar
    }
}