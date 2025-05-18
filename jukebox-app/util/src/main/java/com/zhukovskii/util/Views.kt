package com.zhukovskii.util

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

fun TextView.setTextColorById(resourceId: Int, context: Context) {
    setTextColor(ContextCompat.getColor(context, resourceId))
}

fun View.setBackgroundColorById(resourceId: Int, context: Context) {
    setBackgroundColor(ContextCompat.getColor(context, resourceId))
}

fun ImageView.setTintedDrawableById(drawableResId: Int, colorResId: Int, context: Context) {
    ContextCompat.getDrawable(context, drawableResId)?.let { drawable ->
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        val tint = ContextCompat.getColor(context, colorResId)

        DrawableCompat.setTint(wrappedDrawable, tint)
        setImageDrawable(wrappedDrawable)
    }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}