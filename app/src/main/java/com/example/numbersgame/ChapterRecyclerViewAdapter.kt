package com.example.numbersgame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ChapterRecyclerViewAdapter(private val onClick: (Int) -> Unit) :
    ListAdapter<Chapter, ChapterViewHolder>(ChapterDiffCallback()) {

    override fun submitList(list: List<Chapter>?) {
        // all chapters are unlocked
        list?.let {
            for (chapter in list) {
                chapter.unlocked = true
                if (chapter.requiredScore > chapter.userScore)
                    break
            }
        }
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        return ChapterViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }
}

class ChapterViewHolder private constructor(root: View) : RecyclerView.ViewHolder(root) {
    private val name = root.findViewById<TextView>(R.id.name)
    private val description = root.findViewById<TextView>(R.id.description)
    private val progress = root.findViewById<TextView>(R.id.progress)

    fun bind(chapter: Chapter, onClick: (Int) -> Unit) {
        if (chapter.unlocked) {
            description.text = chapter.description
            name.setTextColor(ContextCompat.getColor(name.context, R.color.colorOnPrimary))
            description.visibility = View.VISIBLE
            progress.text = itemView.context.getString(
                R.string.chapter_record,
                chapter.userScore,
                chapter.requiredScore
            )
            itemView.isEnabled = true
        }
        else {
            description.visibility = View.GONE
            name.setTextColor(ContextCompat.getColor(name.context, R.color.colorOnPrimary))
            itemView.isEnabled = false
            progress.text = "locked"
        }

        name.text = chapter.name
        itemView.setOnClickListener { onClick(chapter.id) }
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

data class Chapter(
    val id: Int,
    val name: String,
    val description: String,
    val requiredScore: Int,
    val userScore: Int
) {
    var unlocked = false
}