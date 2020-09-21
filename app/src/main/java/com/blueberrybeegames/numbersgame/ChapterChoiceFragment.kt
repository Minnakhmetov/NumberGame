package com.blueberrybeegames.numbersgame

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import com.blueberrybeegames.numbersgame.databinding.FragmentChapterChoiceBinding
import com.blueberrybeegames.numbersgame.gamemodes.GameModeViewModel
import com.blueberrybeegames.numbersgame.models.ChapterRepository
import com.blueberrybeegames.numbersgame.utils.getAttr
import kotlinx.android.synthetic.main.fragment_chapter_choice.*
import timber.log.Timber

class ChapterChoiceFragment : Fragment() {

    private lateinit var binding: FragmentChapterChoiceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChapterChoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsIcon.setOnClickListener {
            findNavController().navigate(ChapterChoiceFragmentDirections.actionChapterChoiceFragmentToAboutFragment())
        }

        val chapterList = ChapterRepository(requireContext()).getChapters()

        binding.modePicker.onItemChangeListener = { mode ->
            if (mode == context?.getString(R.string.training_category)) {
                binding.chapterPicker.visibility = View.INVISIBLE
            }
            else {
                binding.chapterPicker.visibility = View.VISIBLE
            }
            binding.chapterPicker.itemList = chapterList.filter { it.category == mode }.map { it.name }
        }

        binding.chapterPicker.onItemChangeListener = { name ->
            chapterList.find { it.category == modePicker.getCurrentItem() && it.name == name }?.let {
                if (it.userScore == -1) {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.percentage.visibility = View.INVISIBLE
                }
                else {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.percentage.visibility = View.VISIBLE
                    binding.percentage.text = "${it.userScore.toString()}%"

                    ObjectAnimator.ofInt(
                        binding.progressBar,
                        "progress",
                        GameModeViewModel.getPercentage(it.userScore)
                    ).apply {
                        interpolator = DecelerateInterpolator()
                        duration = (context?.resources?.getInteger(R.integer.progress_bar_anim_duration) ?: 200).toLong()
                    }.start()
                }
            }
        }

        binding.modePicker.itemList = chapterList.map { it.category }.distinct()

        startButton.setOnClickListener {
            val category = binding.modePicker.getCurrentItem()
            val name = binding.chapterPicker.getCurrentItem()

            Timber.i("$category $name")

            chapterList.find { it.category == category && it.name == name}?.let {
                findNavController().navigate(
                    ChapterChoiceFragmentDirections.actionChapterChoiceFragmentToGameFragment(it.chapterId)
                )
            }
        }
    }

}