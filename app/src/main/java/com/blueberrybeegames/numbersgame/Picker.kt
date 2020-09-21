package com.blueberrybeegames.numbersgame

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextSwitcher
import androidx.constraintlayout.widget.ConstraintLayout

class Picker: ConstraintLayout {
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    private val currentItem: TextSwitcher
    private val nextButton: ImageView
    private val prevButton: ImageView

    init {
        inflate(context, R.layout.picker, this)
        currentItem = findViewById(R.id.currentItem)

        nextButton = findViewById(R.id.nextButton)
        prevButton = findViewById(R.id.prevButton)

        nextButton.setOnClickListener {
            currentItem.inAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right_picker)
            currentItem.outAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_left_picker)
            position++
        }
        prevButton.setOnClickListener {
            currentItem.inAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left_picker)
            currentItem.outAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_right_picker).apply {
                duration = context.resources.getInteger(R.integer.picker_item_change_duration).toLong()
            }
            position--
        }
    }

    var position = 0
    private set(value) {
        if (value >= 0 && value < itemList.size) {
            field = value
            currentItem.setText(itemList[value])
            onItemChangeListener?.invoke(itemList[value])

            prevButton.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
            nextButton.visibility = if (position == itemList.size - 1) View.INVISIBLE else View.VISIBLE
        }
    }

    var itemList = listOf<String>()
    set(value) {
        currentItem.inAnimation = null
        currentItem.outAnimation = null
        field = value
        position = 0
    }

    fun getCurrentItem(): String = itemList.getOrElse(position) { "" }

    var onItemChangeListener: ((String) -> Unit)? = null
}