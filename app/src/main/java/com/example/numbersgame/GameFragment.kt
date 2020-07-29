package com.example.numbersgame

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.numbersgame.databinding.FragmentGameBinding
import com.example.numbersgame.gamemodes.*
import kotlin.properties.Delegates

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameModeViewModel

    private lateinit var mistakeFrameAnimation: Animation

    private var chapterId by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        chapterId = GameFragmentArgs.fromBundle(requireArguments()).id

        viewModel = ViewModelProvider(this).get(
            when (chapterId) {
                1 -> TextModeViewModel::class.java
                2 -> VoiceModeViewModel::class.java
                3 -> OneMistakeTextModeViewModel::class.java
                else -> OneMistakeVoiceModeViewModel::class.java
            }
        )

        binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.startCountdown()

        viewModel.gameFinishEvent.observe(viewLifecycleOwner, Observer { isFinished ->
            if (isFinished?.getContentIfNotHandled() == true) {
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToResultsFragment(viewModel.finalScore, chapterId))
            }
        })

        viewModel.mistake.observe(viewLifecycleOwner, Observer {
            if (it)
                checkIfMistakeAndShowFrame()
            else
                stopMistakeAnimation()
        })

        mistakeFrameAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_out)

        viewModel.words.observe(viewLifecycleOwner, Observer { (isNew, number) ->
            if (isNew)
                binding.words.setText(number)
            else
                binding.words.setCurrentText(number)
        })
    }

    fun checkIfMistakeAndShowFrame() {
        if (viewModel.mistake.value == true) {
            binding.mistakeFrame.visibility = View.VISIBLE
            mistakeFrameAnimation.reset()
            binding.mistakeFrame.startAnimation(mistakeFrameAnimation)
        }
    }

    fun stopMistakeAnimation() {
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