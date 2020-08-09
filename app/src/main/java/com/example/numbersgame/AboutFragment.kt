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
import android.widget.TextView
import android.widget.Toast
import com.example.numbersgame.databinding.FragmentAboutBinding
import com.example.numbersgame.storage.ThemeKeeper
import com.example.numbersgame.utils.getAttr

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
        binding.themeHeader.findViewById<TextView>(R.id.title).text = getString(R.string.theme_header)

        val createdByStyledString = SpannableString(getString(R.string.created_by))
        createdByStyledString.setSpan(
            ForegroundColorSpan(requireContext().getAttr(R.attr.colorSecondary)),
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

        val themeList = listOf(
            R.style.Green,
            R.style.Purple,
            R.style.DarkOrange,
            R.style.Red,
            R.style.Teal,
            R.style.Blue,
            R.style.DarkGreen,
            R.style.WhiteBlue
        )

        val themeKeeper = ThemeKeeper.getInstance(requireContext())

        themeList.forEach {
            binding.themePicker.addView(ThemeColorPreview(requireContext()).apply {
                themeId = it
                ticked = themeKeeper.themeId == it
            })
        }
    }
}