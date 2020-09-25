package com.blueberrybeegames.numbersgame.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.blueberrybeegames.numbersgame.R
import com.blueberrybeegames.numbersgame.databinding.ActionListItemBinding
import timber.log.Timber

class ActionListItem(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {
    private lateinit var binding: ActionListItemBinding

    init {
        val layoutInflater = LayoutInflater.from(context)
        binding = ActionListItemBinding.inflate(layoutInflater, this, true)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ActionListItem,
            0, 0).apply {

            try {
                binding.title.text = getString(R.styleable.ActionListItem_title)
                binding.icon.setImageResource(getResourceId(R.styleable.ActionListItem_src, R.drawable.ic_rate))
            } finally {
                recycle()
            }
        }
    }
}