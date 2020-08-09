package com.example.numbersgame.theme

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.numbersgame.R
import com.example.numbersgame.utils.getAttr

class ThemeColorPreview: AppCompatImageView {
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    var ticked: Boolean = false
        set(value) {
            field = value

            if (ticked) {
                val tickImage = ContextCompat.getDrawable(context,
                    R.drawable.tick_image
                )?.apply {
                    setTint(contextThemeWrapper.getAttr(R.attr.colorOnPrimary))
                }
                setImageDrawable(tickImage)
            }
            else {
                setImageDrawable(null)
            }

            invalidate()
            requestLayout()
        }

    private var contextThemeWrapper = ContextThemeWrapper(context,
        R.style.Green
    )

    var themeId = R.style.Green
        set(value) {
            field = value
            contextThemeWrapper = ContextThemeWrapper(context, themeId)

            val newBackground = ContextCompat.getDrawable(context,
                R.drawable.theme_color_preview
            ) as GradientDrawable
            newBackground.setColor(
                contextThemeWrapper.getAttr(R.attr.colorPrimary)
            )

            newBackground.setStroke(
                context.resources.getDimension(R.dimen.theme_preview_color_stroke).toInt(),
                contextThemeWrapper.getAttr(R.attr.colorSecondary)
            )

            background = newBackground
            invalidate()
            requestLayout()
        }

    init {
        themeId = R.style.Green

        setOnClickListener {
            ThemeKeeper.getInstance(context).themeId = themeId
        }

        val newLayoutParams = GridLayout.LayoutParams(
            ViewGroup.LayoutParams(
                context.resources.getDimension(R.dimen.theme_color_preview_size).toInt(),
                context.resources.getDimension(R.dimen.theme_color_preview_size).toInt()
            )
        )

        newLayoutParams.setMargins(
            context.resources.getDimension(R.dimen.spacing_small).toInt(),
            context.resources.getDimension(R.dimen.spacing_small).toInt(),
            0,
            0
        )

        layoutParams = newLayoutParams

        adjustViewBounds = true

        scaleType = (ScaleType.CENTER)
    }
}