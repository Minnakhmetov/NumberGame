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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.blueberrybeegames.numbersgame.databinding.FragmentAboutBinding
import com.blueberrybeegames.numbersgame.theme.ThemeColorPreview
import com.blueberrybeegames.numbersgame.theme.ThemeKeeper
import com.blueberrybeegames.numbersgame.utils.getAttr
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

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

        val themeKeeper = ThemeKeeper.getInstance(requireContext())

        val themeColorPreviewList: List<ThemeColorPreview> = listOf(
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
        ).map {
            val constraintLayoutParams = ConstraintLayout.LayoutParams(
                0,
                0
            ).apply {
                dimensionRatio = "1:1"
                matchConstraintPercentWidth = 0.15f
            }

            ThemeColorPreview(requireContext()).apply {
                themeId = it
                layoutParams = constraintLayoutParams
                id = View.generateViewId()

                if (themeKeeper.themeId == themeId)
                    ticked = true
            }
        }

        val themeTableRows = themeColorPreviewList.chunked(5)

        themeColorPreviewList.forEach { binding.rootConstraintLayout.addView(it) }

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.rootConstraintLayout)

        constraintSet.createHorizontalChainRtl(
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END,
            themeTableRows[0].map { it.id }.toIntArray(),
            null,
            ConstraintSet.CHAIN_PACKED
        )

        constraintSet.createVerticalChain(
            R.id.theme_header,
            ConstraintSet.BOTTOM,
            R.id.guideline2,
            ConstraintSet.TOP,
            themeTableRows.map { it[0].id }.toIntArray(),
            null,
            ConstraintSet.CHAIN_PACKED
        )

        for (i in 1 until themeTableRows.size) {
            for (j in themeTableRows[i].indices) {
                constraintSet.connect(
                    themeTableRows[i][j].id,
                    ConstraintSet.START,
                    themeTableRows[0][j].id,
                    ConstraintSet.START
                )
                constraintSet.connect(
                    themeTableRows[i][j].id,
                    ConstraintSet.END,
                    themeTableRows[0][j].id,
                    ConstraintSet.END
                )
            }
        }

        themeTableRows.forEach { row ->
            for (j in 1 until row.size) {
                constraintSet.connect(
                    row[j].id,
                    ConstraintSet.TOP,
                    row[0].id,
                    ConstraintSet.TOP
                )
                constraintSet.connect(
                    row[j].id,
                    ConstraintSet.BOTTOM,
                    row[0].id,
                    ConstraintSet.BOTTOM
                )
            }
        }

        constraintSet.applyTo(binding.rootConstraintLayout)

        themeTableRows[0].forEach {
            val preview = binding.root.findViewById<ThemeColorPreview>(it.id)
            val params = preview.layoutParams as ConstraintLayout.LayoutParams

            requireContext().let { context ->
                params.marginStart = context.resources.getDimension(R.dimen.spacing_tiny).toInt()
                params.marginEnd = context.resources.getDimension(R.dimen.spacing_tiny).toInt()
            }

            preview.layoutParams = params
        }

        themeTableRows.forEach {
            val preview = binding.root.findViewById<ThemeColorPreview>(it[0].id)
            val params = preview.layoutParams as ConstraintLayout.LayoutParams

            requireContext().let { context ->
                params.topMargin = context.resources.getDimension(R.dimen.spacing_tiny).toInt()
                params.bottomMargin = context.resources.getDimension(R.dimen.spacing_tiny).toInt()
            }

            preview.layoutParams = params
        }
    }
}