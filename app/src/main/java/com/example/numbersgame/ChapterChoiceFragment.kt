package com.example.numbersgame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.numbersgame.databinding.FragmentChapterChoiceBinding
import com.example.numbersgame.models.Chapter
import com.example.numbersgame.models.ChapterRepository
import com.example.numbersgame.storage.RecordsStorage

class ChapterChoiceFragment : Fragment() {

    private lateinit var binding: FragmentChapterChoiceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChapterChoiceBinding.inflate(inflater, container, false)
        val adapter = ChapterRecyclerViewAdapter { chapterId ->
            findNavController().navigate(
                ChapterChoiceFragmentDirections.actionChapterChoiceFragmentToGameFragment(chapterId)
            )
        }

        binding.chaptersList.adapter = adapter

        adapter.submitList(ChapterRepository(requireContext()).getChaptersGroupedByCategory())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(ChapterChoiceFragmentDirections.actionChapterChoiceFragmentToAboutFragment())
        }
    }

}