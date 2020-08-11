package com.example.numbersgame

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.numbersgame.gamemodes.SandboxModeViewModel
import com.example.numbersgame.models.Chapter
import timber.log.Timber
import kotlin.IllegalArgumentException

private const val HEADER = 0
private const val CHAPTER = 1

class ChapterRecyclerViewAdapter(private val onClick: (String) -> Unit) :
    ListAdapter<ListItem, RecyclerView.ViewHolder>(ChapterDiffCallback()) {

    fun submitList(groupedChapters: Map<String, List<Chapter>>) {
        val itemList = mutableListOf<ListItem>()

        for (group in groupedChapters) {
            itemList.add(ListItem.Header(group.key))
            group.value.forEach { itemList.add(ListItem.ChapterItem(it)) }
        }

        submitList(itemList)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.Header -> HEADER
            is ListItem.ChapterItem -> CHAPTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> HeaderViewHolder.from(parent)
            CHAPTER -> ChapterViewHolder.from(parent)
            else -> throw IllegalArgumentException("unknown viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(getItem(position) as ListItem.Header)
            is ChapterViewHolder -> holder.bind(getItem(position) as ListItem.ChapterItem, onClick)
            else -> throw IllegalArgumentException("unknown ViewHolder")
        }
    }
}

class HeaderViewHolder private constructor(root: View) : RecyclerView.ViewHolder(root) {
    private val name = root.findViewById<TextView>(R.id.title)

    fun bind(header: ListItem.Header) {
        name.text = header.name
    }

    companion object {
        fun from(parent: ViewGroup): HeaderViewHolder {
            return HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_header, parent, false)
            )
        }
    }
}

class ChapterViewHolder private constructor(root: View) : RecyclerView.ViewHolder(root) {
    private val name = root.findViewById<TextView>(R.id.name)
    private val progress = root.findViewById<TextView>(R.id.progress)


    fun bind(item: ListItem.ChapterItem, onClick: (String) -> Unit) {

        name.text = item.chapter.name
        if (item.chapter.chapterId != SandboxModeViewModel.CHAPTED_ID)
            progress.text = item.chapter.userScore.toString()
        else
            progress.text = ""
        itemView.setOnClickListener { onClick(item.chapter.chapterId) }
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

class ChapterDiffCallback : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem.itemId == newItem.itemId
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem == newItem
    }
}

sealed class ListItem(val itemId: String) {

    data class Header(
        val name: String
    ) : ListItem(name)

    data class ChapterItem(
        val chapter: Chapter
    ) : ListItem(chapter.chapterId)
}

