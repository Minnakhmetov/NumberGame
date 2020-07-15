package com.example.numbergame

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.numbergame.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var startCountDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCountDownTimer = object: CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.startCountdown.text = (millisUntilFinished / 1000 + 1).toString()
            }

            override fun onFinish() {
                binding.startCountdown.visibility = View.GONE
                startGame()
            }
        }

        startCountDownTimer.start()
    }

    private fun startGame() {
        binding.textViewGame.visibility = View.VISIBLE
    }

    // TODO Pause timer when activity isn't in focus

}