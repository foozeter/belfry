package com.jamjamucho.belfry

import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.widget.FrameLayout

class BottomPanel(
    context: Context, attrs: AttributeSet)
    : FrameLayout(context, attrs) {

    companion object {
        private const val DEFAULT_PEEK_HEIGHT = 0 // dp
    }

    private val peekHeight: Int

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.BottomPanel, 0, 0)

        val density = context.resources.displayMetrics.density

        peekHeight = a.getDimensionPixelSize(
            R.styleable.BottomPanel_belfry_peekHeight,
            (density * DEFAULT_PEEK_HEIGHT).toInt())

        a.recycle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val offset = bottom - peekHeight - this.top
        ViewCompat.offsetTopAndBottom(this, offset)
    }
}