package com.example.numbersgame

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.numbersgame.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentAboutBinding.inflate(inflater, container, false)

        binding.creditsHeader.findViewById<TextView>(R.id.title).text = getString(R.string.credits_header)
        binding.feedbackHeader.findViewById<TextView>(R.id.title).text = getString(R.string.feedback_header)

        val createdByStyledString = SpannableString(getString(R.string.created_by))
        createdByStyledString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorSecondary)),
            0,
            10,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.createdByText.text = createdByStyledString

        return binding.root
    }
}