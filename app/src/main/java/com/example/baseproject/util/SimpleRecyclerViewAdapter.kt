package com.example.baseproject.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer


class SimpleRecyclerViewAdapter<T>(
    val context: Context,
    val provider: RecyclerProvider<T>
) : RecyclerView.Adapter<SimpleRecyclerViewAdapter<T>.BaseViewHolder>() {

    init {
        provider.adapter = this
    }

    abstract class RecyclerProvider<T> {
        var adapter: SimpleRecyclerViewAdapter<T>? = null

        var items: List<T> = listOf()
            set(value) {
                val oldValue = field
                field = value
                adapter?.let { setItem(it, oldValue, value) }
            }

        abstract fun getLayoutId(): Int
        open fun getItemCount(): Int {
            return items.size
        }

        open fun getItem(position: Int): T {
            return items[position]
        }

        abstract fun onBindView(containerView: View, item: T)
        abstract fun onClick(adapterPosition: Int)
        open fun setItem(
            adapter: SimpleRecyclerViewAdapter<T>,
            oldItems: List<T>,
            newItems: List<T>
        ) {
            notifyIfChanged(adapter, oldItems, newItems)
        }
    }

    inner class BaseViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun onBindView(item: T) {
            provider.onBindView(containerView, item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val rootView: View = LayoutInflater.from(context)
            .inflate(provider.getLayoutId(), parent, false)
        val holder = BaseViewHolder(rootView)

        holder.containerView.setOnClickListener {
            provider.onClick(holder.adapterPosition)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return provider.getItemCount()
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBindView(provider.getItem(position))
    }


}


fun <T> notifyIfChanged(adapter: RecyclerView.Adapter<*>, old: List<T>, new: List<T>) {

    var removedItemCount = 0

    old.forEachIndexed { index, oldMission ->
        if (!new.contains(oldMission)) {
            adapter.notifyItemRemoved(index - removedItemCount)
            removedItemCount++
        }
    }

    new.forEachIndexed { index, newMission ->
        if (!old.contains(newMission)) {
            adapter.notifyItemInserted(index)
        }
    }

}