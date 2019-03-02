package com.jamjamucho.belfry

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class AnimatableOptionsIcon(
    context: Context, attributeSet: AttributeSet)
    : AnimatableIcon(R.drawable.ic_close, R.drawable.ic_options, context, attributeSet) {

    override fun onProgressChanged(progress: Float, front: ImageView, back: ImageView) {
        back.scaleX = progress
        back.scaleY = progress
        back.alpha = progress
        front.alpha = 1f - progress
        front.scaleY = front.alpha
    }
}