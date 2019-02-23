package com.jamjamucho.belfry

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.v4.math.MathUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView

abstract class AnimationIcon(
    @DrawableRes backIconRes: Int,
    @DrawableRes frontIconRes: Int,
    context: Context, attributeSet: AttributeSet)
    : FrameLayout(context, attributeSet) {

    var progress = 0f
        set(value) {
            val new = MathUtils.clamp(value, 0f , 1f)
            if (field != new) {
                field = new
                performOnProgressChanged(new)
            }
        }

    companion object {
        private const val ICON_SIZE = 24 // dp
    }

    private val iconSize = (context.resources.displayMetrics.density * ICON_SIZE).toInt()

    private val frontIcon = ImageView(context).apply {
        layoutParams = generateIconLayoutParams()
        setImageResource(frontIconRes)
    }

    private val backIcon = ImageView(context).apply {
        layoutParams = generateIconLayoutParams()
        setImageResource(backIconRes)
    }

    init {
        addView(backIcon)
        addView(frontIcon)
    }

    protected abstract fun onProgressChanged(progress: Float, front: ImageView, back: ImageView)

    private fun performOnProgressChanged(progress: Float) = onProgressChanged(progress, frontIcon, backIcon)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // Setup initial position
        performOnProgressChanged(0f)
    }

    private fun generateIconLayoutParams() =
        LayoutParams(iconSize, iconSize).apply { gravity = Gravity.CENTER }

    private fun exactSpec(size: Int) =
        MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)

    final override fun addView(child: View?) = super.addView(child)
    final override fun addView(child: View?, index: Int) = super.addView(child, index)
    final override fun addView(child: View?, width: Int, height: Int) = super.addView(child, width, height)
    final override fun addView(child: View?, params: ViewGroup.LayoutParams?) = super.addView(child, params)
    final override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) = super.addView(child, index, params)

    fun setColorFilter(color: Int) {
        frontIcon.setColorFilter(color)
        backIcon.setColorFilter(color)
    }
}