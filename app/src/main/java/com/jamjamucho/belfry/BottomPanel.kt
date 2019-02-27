package com.jamjamucho.belfry

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.widget.FrameLayout

class BottomPanel(
    context: Context, attrs: AttributeSet)
    : FrameLayout(context, attrs) {

    companion object {
        private const val DEFAULT_PEEK_HEIGHT = 0 // dp
        private const val DEFAULT_CONCAVE_RADIUS = 16 //dp
        private const val DEFAULT_CONCAVE_BACKGROUND_COLOR = Color.TRANSPARENT
    }

    // This will be initialized in onLayout() method.
    private var collapsedTop = 0

    private var isCornerRadiusChanged = true

    private val peekHeight: Int

    private val outline = Path()

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    var cornerRadius = 0f
        private set(radius) {
            if (field != radius) {
                field = radius
                isCornerRadiusChanged = true
                // If radius > 0, this view has a convex shape,
                // if radius < 0, this view has a concave shape.
                // Let children stay within the outline of this view by adding the top padding.
                setPadding(paddingLeft, -radius.toInt(), paddingRight, paddingBottom)
            }
        }

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.BottomPanel, 0, 0)

        val backgroundColor = a.getColor(
            R.styleable.BottomPanel_belfry_backgroundColor,
            DEFAULT_CONCAVE_BACKGROUND_COLOR)

        val density = context.resources.displayMetrics.density

        cornerRadius = a.getDimensionPixelSize(
            R.styleable.BottomPanel_belfry_cornerRadius,
            (density * DEFAULT_CONCAVE_RADIUS).toInt())
            .toFloat()

        peekHeight = a.getDimensionPixelSize(
            R.styleable.BottomPanel_belfry_peekHeight,
            (density * DEFAULT_PEEK_HEIGHT).toInt())

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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val offset = bottom - peekHeight - this.top
        ViewCompat.offsetTopAndBottom(this, offset)
        collapsedTop = this.top
    }

    fun expand() {
        ViewCompat.offsetTopAndBottom(this, -top)
    }

    fun collapse() {
        ViewCompat.offsetTopAndBottom(this, collapsedTop - top)
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
}