package com.example.numbersgame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.numbersgame.databinding.FragmentChapterChoiceBinding
import com.example.numbersgame.storage.RecordsStorage

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

        binding.chaptersList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        binding.chaptersList.adapter = adapter

        val recordsStorage =
            RecordsStorage(requireContext())

        adapter.submitList(listOf(
            Chapter(
                1,
                "Chapter 1",
                "Simplest one. Just do it.",
                7,
                recordsStorage.getRecord(1)
            ),
            Chapter(
                2,
                "Chapter 2",
                "A single mistake can cost a lot, right? No hints this time.",
                7,
                recordsStorage.getRecord(2)
            ),
            Chapter(
                3,
                "Chapter 3",
                "Be careful, takes a lot of attention.",
                7,
                recordsStorage.getRecord(3)
            )
        ))

        return binding.root
    }

}