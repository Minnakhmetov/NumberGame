package com.blueberrybeegames.numbersgame

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.blueberrybeegames.numbersgame.databinding.FragmentAboutBinding
import com.blueberrybeegames.numbersgame.theme.ThemeColorPreview
import com.blueberrybeegames.numbersgame.theme.ThemeKeeper
import com.blueberrybeegames.numbersgame.utils.getAttr
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

    private fun getCreateByStyledText(): SpannableString {
        val createdByStyledString = SpannableString(getString(R.string.created_by))

        var pos = createdByStyledString.length

        repeat(3) {
            pos--;
            pos = createdByStyledString.lastIndexOf(' ', pos)
        }

        if (pos != -1) {
            createdByStyledString.setSpan(
                ForegroundColorSpan(requireContext().getAttr(R.attr.colorSecondary)),
                0,
                pos,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return createdByStyledString
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.creditsHeader.findViewById<TextView>(R.id.title).text = getString(R.string.credits_header)
        binding.feedbackHeader.findViewById<TextView>(R.id.title).text = getString(R.string.feedback_header)
        binding.themeHeader.findViewById<TextView>(R.id.title).text = getString(R.string.theme_header)



        binding.createdByText.text = getCreateByStyledText()

        binding.soundCredits.movementMethod = LinkMovementMethod.getInstance()

        binding.emailButton.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:whiteplum42@gmail.com?subject=${Uri.encode("Numbers in English support")}")
            }

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
            else {
                val whiteText = SpannableString(getString(R.string.no_email_app)).apply {
                    setSpan(
                        ForegroundColorSpan(Color.WHITE),
                        0,
                        length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                Snackbar.make(it, whiteText, Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.rateButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=com.blueberrybeegames.numbersgame")
            startActivity(intent)
        }

        val themeList = listOf(
            R.style.DarkOrange,
            R.style.WhiteBlue,
            R.style.Green,
            R.style.Purple,
            R.style.Red,
            R.style.Teal,
            R.style.Blue,
            R.style.DarkGreen,
            R.style.Brown,
            R.style.New
        )

        val themeKeeper = ThemeKeeper.getInstance(requireContext())

        themeList.forEach {
            binding.themePicker.addView(
                ThemeColorPreview(
                    requireContext()
                ).apply {
                themeId = it
                ticked = themeKeeper.themeId == it
            })
        }
    }
}