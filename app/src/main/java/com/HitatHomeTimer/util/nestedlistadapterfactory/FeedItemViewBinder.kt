package com.HitatHomeTimer.util.nestedlistadapterfactory

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class FeedItemViewBinder<Model, in MViewHolder: RecyclerView.ViewHolder>(
    val modelClass: Class<out Model>) : DiffUtil.ItemCallback<Model>() {

        abstract fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
        abstract fun bindViewHolder(model: Model, viewHolder: MViewHolder)
        abstract fun getFeedItemType(): Int

        open fun onViewRecycled(viewHolder: MViewHolder) = Unit
        open fun onViewDetachedFromWindow(viewHolder: MViewHolder) = Unit
}