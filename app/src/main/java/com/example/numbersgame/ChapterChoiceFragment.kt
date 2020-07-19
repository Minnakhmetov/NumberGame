package com.example.numbersgame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.numbersgame.R
import com.example.numbersgame.databinding.FragmentChapterChoiceBinding

class ChapterChoiceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentChapterChoiceBinding.inflate(inflater, container, false)
        val adapter = ChapterRecyclerViewAdapter { chapterId ->
            findNavController().navigate(
                ChapterChoiceFragmentDirections.actionChapterChoiceFragmentToGameFragment(chapterId)
            )
        }
        binding.chaptersList.adapter = adapter

        adapter.submitList(listOf(
            Chapter(
                1,
                "Chapter 1",
                "Simplest one. Just do it."
            ),
            Chapter(
                2,
                "Chapter 2",
                "A single mistake can cost a lot, right? No hints this time."
            )
        ))

        return binding.root
    }

}