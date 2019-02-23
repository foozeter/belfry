package com.jamjamucho.belfry

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

class AppBar(context: Context, attrs: AttributeSet)
    : FrameLayout(context, attrs) {

    companion object {
        private const val DESIRED_HEIGHT = 56 // dp
        private const val ELEVATION_SCROLL_DISTANCE = 56 // dp
        private const val MAX_ELEVATION = 4 // dp
    }

    val menuIcon: ImageView
    val optionsIcon: ImageView

    init {
        View.inflate(context, R.layout.app_bar, this)
        menuIcon = findViewById(R.id.menu_icon)
        optionsIcon = findViewById(R.id.options_icon)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = (context.resources.displayMetrics.density * DESIRED_HEIGHT).toInt()
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if (heightMode == MeasureSpec.UNSPECIFIED ||
            (heightMode == MeasureSpec.AT_MOST && desiredHeight <= heightSize)
        ) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(desiredHeight, MeasureSpec.EXACTLY))
        } else super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun makeScrollViewElevationRelationShip(scrollView: NestedScrollView) {
        scrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {

            private val elevationScrollDistance
                    = context.resources.displayMetrics.density * ELEVATION_SCROLL_DISTANCE

            private val maxElevation
                    = context.resources.displayMetrics.density * MAX_ELEVATION

            override fun onScrollChange(view: NestedScrollView,
                                        scrollX: Int, scrollY: Int,
                                        oldScrollX: Int, oldScrollY: Int) {

                val f = scrollY / elevationScrollDistance
                ViewCompat.setElevation(this@AppBar, Math.min(maxElevation, f * maxElevation))
            }
        })
    }
}