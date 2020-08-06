package com.example.numbersgame

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.numbersgame.databinding.FragmentAboutBinding
import com.google.android.material.snackbar.Snackbar

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.emailButton.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:whiteplum42@gmail.com?subject=${Uri.encode("Numbers Game support")}")
            }

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
            else {
                Toast.makeText(context, R.string.no_email_app, Toast.LENGTH_SHORT).show()
            }
        }
    }
}