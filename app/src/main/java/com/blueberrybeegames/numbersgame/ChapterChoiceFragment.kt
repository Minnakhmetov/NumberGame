package com.blueberrybeegames.numbersgame

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.navigation.fragment.findNavController
import com.blueberrybeegames.numbersgame.databinding.FragmentChapterChoiceBinding
import com.blueberrybeegames.numbersgame.gamemodes.GameModeViewModel
import com.blueberrybeegames.numbersgame.models.ChapterRepository
import kotlinx.android.synthetic.main.circle_progress_bar.view.*
import kotlinx.android.synthetic.main.fragment_chapter_choice.*
import timber.log.Timber

class ChapterChoiceFragment : Fragment() {

    private lateinit var binding: FragmentChapterChoiceBinding
    private var chapterPickerPosition = mutableMapOf<String, Int>()
    private var modePickerSavedPos = 0

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

            binding.chapterPicker.apply {
                setItemList(
                    chapterList.filter { it.category == mode }.map { it.name },
                    chapterPickerPosition[mode] ?: 0
                )
            }
        }

        binding.chapterPicker.onItemChangeListener = { name ->
            chapterPickerPosition[modePicker.getCurrentItem()] = binding.chapterPicker.position
            Timber.i("+${modePicker.getCurrentItem()} ${chapterPickerPosition[modePicker.getCurrentItem()]}")
            chapterList.find { it.category == modePicker.getCurrentItem() && it.name == name }?.let {
                if (it.userScore == -1) {
                    binding.progressBarWithPercentage.percentage.setText("")
                    changeProgressBarProgress(0)
                }
                else {
                    binding.progressBarWithPercentage.visibility = View.VISIBLE
                    binding.progressBarWithPercentage.percentage.setText("${it.userScore.toString()}%")
                    changeProgressBarProgress(it.userScore)
                }
            }
        }

        binding.modePicker.setItemList(chapterList.map { it.category }.distinct(), 0)

        startButton.setOnClickListener {
            val category = binding.modePicker.getCurrentItem()
            val name = binding.chapterPicker.getCurrentItem()

            chapterList.find { it.category == category && it.name == name}?.let {
                findNavController().navigate(
                    ChapterChoiceFragmentDirections.actionChapterChoiceFragmentToGameFragment(it.chapterId)
                )
            }
        }
    }

    private fun changeProgressBarProgress(newProgress: Int) {
        ObjectAnimator.ofInt(
            binding.progressBarWithPercentage.progressBar,
            "progress",
            GameModeViewModel.getPercentage(newProgress)
        ).apply {
            interpolator = DecelerateInterpolator()
            duration = (context?.resources?.getInteger(R.integer.progress_bar_anim_duration) ?: 200).toLong()
        }.start()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        modePickerSavedPos = binding.modePicker.position
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding.modePicker.position = modePickerSavedPos
        binding.chapterPicker.position = chapterPickerPosition[modePicker.getCurrentItem()] ?: 0
    }

}