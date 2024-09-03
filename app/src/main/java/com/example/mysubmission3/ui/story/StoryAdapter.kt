package com.example.mysubmission3.ui.story

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysubmission3.data.api.response.ListStoryItem
import com.example.mysubmission3.databinding.ItemListBinding

class StoryAdapter(val context: Context) : ListAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    private var onItemClickCallback: OnItemClickCallback? = null

    interface OnItemClickCallback {
        fun  onItemClicked(item: ListStoryItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ViewHolder(val context: Context, val binding: ItemListBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("CheckResult")
        fun bind(listStoryItem: ListStoryItem) {
            Glide.with(context)
                .load("${listStoryItem.photoUrl}")
                .into(binding.ivStory)
            binding.tvName.text = listStoryItem.name
            binding.tvId.text = listStoryItem.id
            itemView.setOnClickListener {
                onItemClickCallback?.onItemClicked(listStoryItem)
            }
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(context, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}