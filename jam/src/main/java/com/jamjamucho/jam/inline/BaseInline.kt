package com.jamjamucho.jam.inline

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

abstract class BaseInline(
    context: Context, attributeSet: AttributeSet)
    : ViewGroup(context, attributeSet) {

    init {
        visibility = View.GONE
        setWillNotDraw(true)
    }

    final override fun setWillNotDraw(willNotDraw: Boolean) {
        super.setWillNotDraw(willNotDraw)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // Do nothing.
    }
}