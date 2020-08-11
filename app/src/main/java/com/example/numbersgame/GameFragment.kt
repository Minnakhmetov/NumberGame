package com.example.numbersgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.numbersgame.databinding.FragmentGameBinding
import com.example.numbersgame.gamemodes.*
import com.example.numbersgame.utils.getAttr

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameModeViewModel

    private lateinit var mistakeFrameAnimation: Animation

    private lateinit var chapterId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chapterId = GameFragmentArgs.fromBundle(requireArguments()).id

        viewModel = ViewModelProvider(this).get(
            when (chapterId) {
                TextModeViewModel.CHAPTER_ID -> TextModeViewModel::class.java
                VoiceModeViewModel.CHAPTER_ID -> VoiceModeViewModel::class.java
                OneMistakeTextModeViewModel.CHAPTER_ID -> OneMistakeTextModeViewModel::class.java
                OneMistakeVoiceModeViewModel.CHAPTER_ID -> OneMistakeVoiceModeViewModel::class.java
                SandboxModeViewModel.CHAPTED_ID -> SandboxModeViewModel::class.java
                else -> throw IllegalArgumentException("unknown chapterId")
            }
        )

        binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playAgainButton.setOnClickListener {
            findNavController().navigate(GameFragmentDirections.actionGameFragmentSelf(chapterId))
        }

        viewModel.initialize()

        viewModel.mistake.observe(viewLifecycleOwner, Observer {
            if (it)
                checkIfMistakeAndShowFrame()
            else
                stopMistakeAnimation()
        })

        mistakeFrameAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_out)

        val secondaryColor = requireContext().getAttr(R.attr.colorSecondary)

        viewModel.words.observe(viewLifecycleOwner, Observer { (isNew, number) ->
            number.applyDelayedSpans(secondaryColor)
            if (isNew)
                binding.words.setText(number)
            else
                binding.words.setCurrentText(number)
        })

        if (chapterId == SandboxModeViewModel.CHAPTED_ID) {
            binding.score.visibility = View.INVISIBLE
            binding.scoreLabel.visibility = View.INVISIBLE
        }

        viewModel.gameState.observe(viewLifecycleOwner, Observer { state ->
            if (state == GameModeViewModel.STARTED) {
                binding.layout.visibility = View.VISIBLE
                binding.layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fragment_open_enter))
            }

            when (state) {
                GameModeViewModel.STARTED -> {
                    binding.gameKeyboard.visibility = View.VISIBLE
                    binding.timer.visibility = View.VISIBLE
                }
                else -> {
                    binding.gameKeyboard.visibility = View.INVISIBLE
                    binding.timer.visibility = View.INVISIBLE
                }
            }

            when (state) {
                GameModeViewModel.FINISHED -> {
                    showGameEndElements()
                }
            }
        })
    }

    private fun showGameEndElements() {
        val gameEndElements = listOf(binding.playAgainButton, binding.gameEndMessage, binding.answer)
        for (el in gameEndElements) {
            el.visibility = View.VISIBLE
            el.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fragment_open_enter))
        }
    }

    private fun checkIfMistakeAndShowFrame() {
        if (viewModel.mistake.value == true) {
            binding.mistakeFrame.visibility = View.VISIBLE
            mistakeFrameAnimation.reset()
            binding.mistakeFrame.startAnimation(mistakeFrameAnimation)
        }
    }

    private fun stopMistakeAnimation() {
        binding.mistakeFrame.visibility = View.INVISIBLE
        mistakeFrameAnimation.cancel()
        binding.mistakeFrame.clearAnimation()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onGamePaused()
        stopMistakeAnimation()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onGameResumed()
        checkIfMistakeAndShowFrame()
    }
}