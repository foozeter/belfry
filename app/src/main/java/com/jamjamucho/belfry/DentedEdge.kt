package com.jamjamucho.belfry

import android.content.Context
import android.graphics.*
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.annotation.Px
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.min

class DentedEdge(
    context: Context, attributeSet: AttributeSet)
    : View(context, attributeSet) {

    companion object {
        private const val START_SHADOW_COLOR = "#37000000"
        private const val END_SHADOW_COLOR = "#03000000"
        private const val SHADOW_SIZE = 3 //dp
        private const val CURVE_RADIUS = 16 //dp
        private const val TINT = Color.WHITE
    }

    @Px
    var curveRadius = 0f
        set(newRadius) {
            if (field != newRadius) {
                field = newRadius
                isAppearanceChanged = true
                if (newRadius + shadowSize <= height) invalidate()
                else requestLayout()
            }
        }

    @ColorInt
    var tint = 0
        set(newTint) {
            if (field != newTint) {
                field = newTint
                curvePaint.color = newTint
                isAppearanceChanged = true
                invalidate()
            }
        }

    @ColorInt
    private val shadowStartColor: Int

    @ColorInt
    private val shadowEndColor: Int

    @Px
    private val shadowSize: Float

    @IdRes
    private val anchorId: Int

    private val shapeBounds = RectF()

    private val curvePaint: Paint

    private val curveShadowPaint: Paint

    private val edgeShadowPaint: Paint

    private val curveEndShadowPaint: Paint

    private val curve = Path()

    private val curveShadow = Path()

    private val edgeShadow = Path()

    private val curveEndShadow = Path()

    private var isAppearanceChanged = true

    private var isFistLayout = true

    init {

        val paintConfig: Paint.() -> Unit = {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        curvePaint = Paint().apply(paintConfig)
        curveShadowPaint = Paint().apply(paintConfig)
        edgeShadowPaint = Paint().apply(paintConfig)
        curveEndShadowPaint = Paint().apply(paintConfig)

        val attrs = context.obtainStyledAttributes(
            attributeSet, R.styleable.DentedEdge, 0, 0)

        curveRadius = attrs.getDimensionPixelSize(
            R.styleable.DentedEdge_belfry_curveRadius,
            (resources.displayMetrics.density * CURVE_RADIUS).toInt())
            .toFloat()

        tint = attrs.getColor(R.styleable.DentedEdge_belfry_tint, TINT)

        shadowSize = attrs.getDimensionPixelSize(
            R.styleable.DentedEdge_belfry_shadowSize,
            (resources.displayMetrics.density * SHADOW_SIZE).toInt())
            .toFloat()

        shadowStartColor = attrs.getColor(
            R.styleable.DentedEdge_belfry_startShadowColor,
            Color.parseColor(START_SHADOW_COLOR))

        shadowEndColor = attrs.getColor(
            R.styleable.DentedEdge_belfry_endShadowColor,
            Color.parseColor(END_SHADOW_COLOR))

        anchorId = attrs.getResourceId(
            R.styleable.DentedEdge_belfry_anchorTo, 0)

        attrs.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        if (isAppearanceChanged) {
            buildCurve(shapeBounds)
            buildEdge(shapeBounds)
            isAppearanceChanged = false
        }
        canvas.apply {
            val saved = save()
            drawPath(edgeShadow, edgeShadowPaint) // Edge shadow
            drawPath(curveShadow, curveShadowPaint) // Left curve shadow
            drawPath(curveEndShadow, curveEndShadowPaint)
            drawPath(curve, curvePaint) // Left curve
            rotate(-90f, 0f, shadowSize)
            translate(-shapeBounds.height(), shapeBounds.width() - curveRadius)
            drawPath(curveShadow, curveShadowPaint) // Right curve shadow
            drawPath(curve, curvePaint) // Right curve
            restoreToCount(saved)
            rotate(-90f, 0f, shadowSize)
            translate(0f, shapeBounds.width())
            drawPath(curveEndShadow, curveEndShadowPaint)
            restoreToCount(saved)
        }
    }

    private fun buildCurve(bounds: RectF) {
        curve.apply {
            reset()
            moveTo(bounds.left, bounds.top)
            arcTo(cx = bounds.left + curveRadius, cy = bounds.top,
                radius = curveRadius, startAngle = 180f, endAngle = 90f)
            lineTo(bounds.left, bounds.top + curveRadius)
        }

        curveShadow.apply {
            reset()
            moveTo(bounds.left, bounds.top)
            arcTo(cx = bounds.left + curveRadius, cy = bounds.top,
                radius = curveRadius, startAngle = 180f, endAngle = 90f)
            rLineTo(0f, -shadowSize)
            arcTo(cx = bounds.left + curveRadius, cy = bounds.top,
                radius = curveRadius - shadowSize, startAngle = 90f, endAngle = 180f)
            rLineTo(-shadowSize, 0f)
        }

        curveEndShadow.apply {
            reset()
            moveTo(bounds.left, bounds.top)
            rLineTo(shadowSize, 0f)
            arcTo(cx = bounds.left, cy = bounds.top,
                radius = shadowSize, startAngle = 0f, endAngle = -90f)
            rLineTo(0f, shadowSize)
        }

        curveShadowPaint.shader = RadialGradient(
            bounds.left + curveRadius, bounds.top, curveRadius,
            intArrayOf(shadowEndColor, shadowEndColor, shadowStartColor),
            floatArrayOf(0f, 1f - shadowSize / curveRadius, 1f),
            Shader.TileMode.CLAMP)

        curveEndShadowPaint.shader = RadialGradient(
            bounds.left, bounds.top, shadowSize,
            shadowStartColor, shadowEndColor,
            Shader.TileMode.CLAMP)
    }

    private fun buildEdge(bounds: RectF) {
        edgeShadow.apply {
            reset()
            moveTo(bounds.left + curveRadius, bounds.top + curveRadius - shadowSize)
            rLineTo(bounds.width() - curveRadius * 2, 0f)
            rLineTo(0f, shadowSize)
            rLineTo(curveRadius * 2 - bounds.width(), 0f)
            rLineTo(0f, -shadowSize)
        }

        edgeShadowPaint.shader = LinearGradient(
            0f, bounds.top + curveRadius, 0f, bounds.top + curveRadius - shadowSize,
            shadowStartColor, shadowEndColor,
            Shader.TileMode.CLAMP)
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)

        setMeasuredDimension(
            when (wMode) {
                MeasureSpec.EXACTLY -> wSize
                MeasureSpec.UNSPECIFIED -> (curveRadius * 2).toInt()
                MeasureSpec.AT_MOST -> min((curveRadius * 2).toInt(), wSize)
                else -> 0
            },
            when (hMode) {
                MeasureSpec.EXACTLY -> hSize
                MeasureSpec.UNSPECIFIED -> (curveRadius + shadowSize).toInt()
                MeasureSpec.AT_MOST -> min((curveRadius + shadowSize).toInt(), hSize)
                else -> 0
            })

        shapeBounds.set(0f, shadowSize, measuredWidth.toFloat(), shadowSize + curveRadius)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val parent = parent as? ViewGroup
        if (isFistLayout && parent != null && 0 < anchorId) {
            isFistLayout = false
            val anchor = parent.findViewById<View>(anchorId)
            onAnchorBoundsChanged(anchor)
            anchor?.addOnLayoutChangeListener {
                    view, _, _, _, _, _, _, _, _ -> onAnchorBoundsChanged(view)
            }
        }
    }

    private fun Path.arcTo(cx: Float, cy: Float, radius: Float, startAngle: Float, endAngle: Float)
            = arcTo(cx - radius, cy - radius, cx + radius, cy + radius, startAngle, endAngle - startAngle, false)

    private fun onAnchorBoundsChanged(anchor: View)
            = ViewCompat.offsetTopAndBottom(this, anchor.top - height - top)
}