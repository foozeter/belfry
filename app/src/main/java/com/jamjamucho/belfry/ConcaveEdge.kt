package com.jamjamucho.belfry

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.support.transition.Transition
import android.support.transition.TransitionValues
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class ConcaveEdge(
    context: Context,
    attributeSet: AttributeSet)
    : View(context, attributeSet) {

    companion object {
        private const val DEFAULT_CONCAVE_RADIUS = 0 //dp
        private const val DEFAULT_CONCAVE_BACKGROUND_COLOR = Color.TRANSPARENT
    }

    private var isCornerRadiusChanged = true

    private val outline = Path()

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    var cornerRadius = 0f
        set(radius) {
            if (field != radius) {
                field = radius
                isCornerRadiusChanged = true
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }

    init {

        val a = context.obtainStyledAttributes(
            attributeSet, R.styleable.BottomPanel, 0, 0)

        val backgroundColor = a.getColor(
            R.styleable.BottomPanel_belfry_backgroundColor,
            DEFAULT_CONCAVE_BACKGROUND_COLOR)

        val density = context.resources.displayMetrics.density

        cornerRadius = a.getDimensionPixelSize(
            R.styleable.BottomPanel_belfry_cornerRadius,
            (density * DEFAULT_CONCAVE_RADIUS).toInt())
            .toFloat()
        a.recycle()

        super.setBackgroundColor(Color.TRANSPARENT)
        setBackgroundColor(backgroundColor)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        makeOutlinePath()
        canvas.drawPath(outline, paint)
    }

    override fun setBackgroundColor(color: Int) {
        paint.color = color
        invalidate()
    }

    private fun makeOutlinePath() {
        val notChanged = !isCornerRadiusChanged
        val radius = cornerRadius
        isCornerRadiusChanged = false
        when {

            notChanged -> return

            // convex shape
            0 < radius -> outline.apply {
                val r = radius
                reset()
                moveTo(0f, 2 * r)
                arcTo(0f, r, 2 * r, 3 * r, 180f, 90f, true)
                lineTo(width - r, r)
                arcTo(width - 2 * r, r, width.toFloat(), 3 * r, 270f, 90f, true)
                lineTo(width.toFloat(), height.toFloat())
                lineTo(0f, height.toFloat())
                lineTo(0f, 2 * r)
                close()
            }

            // concave shape
            else -> outline.apply {
                val r = -radius
                reset()
                moveTo(0f, 0f)
                arcTo(0f, -r, 2 * r, r, 180f, -90f, true)
                lineTo(width - r, r)
                arcTo(width - 2 * r, -r, width.toFloat(), r, 90f, -90f, true)
                lineTo(width.toFloat(), height.toFloat())
                lineTo(0f, height.toFloat())
                lineTo(0f, 0f)
                close()
            }
        }
    }

    class ChangeCornerRadius: Transition() {

        companion object {
            private const val PROP_PROGRESS = "com.jamjamucho.belfry:BottomPanel:ChangeCornerRadius"
        }

        override fun captureStartValues(transitionValues: TransitionValues)
                = captureValues(transitionValues)

        override fun captureEndValues(transitionValues: TransitionValues)
                = captureValues(transitionValues)

        private fun captureValues(transitionValues: TransitionValues) {
            val view = transitionValues.view
            if (view is ConcaveEdge) {
                transitionValues.values[PROP_PROGRESS] = view.cornerRadius
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
            val view = startValues.view as? ConcaveEdge
            view ?: return null
            return ValueAnimator.ofFloat(start, end).apply {
                addUpdateListener {
                    view.cornerRadius = it.animatedValue as Float
                }
            }
        }
    }
}