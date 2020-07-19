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

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel

    private lateinit var mistakeFrameAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        binding = FragmentGameBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.startCountdown()
        viewModel.gameStartEvent.observe(viewLifecycleOwner, Observer {
            if (it?.getContentIfNotHandled() == true) {
                showInterface()
                viewModel.startGameTimer()
                viewModel.startNewRound()
            }
        })

        viewModel.gameFinishEvent.observe(viewLifecycleOwner, Observer { isFinished ->
            if (isFinished?.getContentIfNotHandled() == true) {
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToResultsFragment(viewModel.finalScore))
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
        viewModel.stopGameTimer()
        viewModel.stopCountdown()
        stopMistakeAnimation()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumeGameTimer()
        viewModel.resumeCountdown()
        checkIfMistakeAndShowFrame()
    }

    private fun showInterface() {
        binding.gameInterface.visibility = View.VISIBLE
        binding.startCountdown.visibility = View.INVISIBLE
    }
}