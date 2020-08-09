package com.example.numbersgame.utils

import android.content.Context
import android.util.TypedValue

fun Context.getAttr(attr: Int): Int {
    val typedValue = TypedValue();
    theme.resolveAttribute(attr, typedValue, true);
    return typedValue.data
}