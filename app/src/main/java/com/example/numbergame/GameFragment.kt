package com.example.numbergame

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.numbergame.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel

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
            if (it) {
                showInterface()
                viewModel.onGameStarted()
                viewModel.startGameTimer()
                viewModel.startNewRound()
            }
        })

    }

    override fun onPause() {
        super.onPause()
        viewModel.stopGameTimer()
        viewModel.stopCountdown()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumeGameTimer()
        viewModel.resumeCountdown()
    }

    private fun showInterface() {
        binding.gameInterface.visibility = View.VISIBLE
        binding.startCountdown.visibility = View.INVISIBLE
    }
}