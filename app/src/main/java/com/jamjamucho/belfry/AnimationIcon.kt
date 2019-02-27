package com.jamjamucho.belfry

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.support.annotation.DrawableRes
import android.support.transition.Transition
import android.support.transition.TransitionValues
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
        val attrs = context.obtainStyledAttributes(attributeSet, R.styleable.AnimationIcon, 0, 0)
        setColorFilter(attrs.getColor(R.styleable.AnimationIcon_belfry_iconTint, Color.BLACK))
        attrs.recycle()
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

    class ChangeProgress: Transition() {

        companion object {
            private const val PROP_PROGRESS = "com.jamjamucho.belfry:AnimationIcon.ChangeProgress:progress"
        }

        override fun captureStartValues(transitionValues: TransitionValues)
                = captureValues(transitionValues)

        override fun captureEndValues(transitionValues: TransitionValues)
                = captureValues(transitionValues)

        private fun captureValues(transitionValues: TransitionValues) {
            val view = transitionValues.view
            if (view is AnimationIcon) {
                transitionValues.values[PROP_PROGRESS] = view.progress
            }
        }

        override fun createAnimator(
            sceneRoot: ViewGroup,
            startValues: TransitionValues?,
            endValues: TransitionValues?): Animator? {
            startValues ?: return null
            endValues ?: return null
            val start = startValues.values[PROP_PROGRESS] as? Float
            val end = endValues.values[PROP_PROGRESS] as? Float
            start ?: return null
            end ?: return null
            val view = startValues.view as? AnimationIcon
            view ?: return null
            return ValueAnimator.ofFloat(start, end).apply {
                addUpdateListener {
                    view.progress = it.animatedValue as Float
                }
            }
        }
    }
}