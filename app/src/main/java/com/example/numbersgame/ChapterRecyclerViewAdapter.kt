package com.example.numbersgame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ChapterRecyclerViewAdapter :
    ListAdapter<Chapter, ChapterViewHolder>(ChapterDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        return ChapterViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ChapterViewHolder private constructor(root: View) : RecyclerView.ViewHolder(root) {
    private val name = root.findViewById<TextView>(R.id.name)
    private val description = root.findViewById<TextView>(R.id.description)

    fun bind(chapter: Chapter) {
        name.text = chapter.name
        description.text = chapter.description
    }

    companion object {
        fun from(parent: ViewGroup): ChapterViewHolder {
            return ChapterViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.chapter_item_list, parent, false)
            )
        }
    }
}

class ChapterDiffCallback : DiffUtil.ItemCallback<Chapter>() {
    override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
        return oldItem == newItem
    }
}