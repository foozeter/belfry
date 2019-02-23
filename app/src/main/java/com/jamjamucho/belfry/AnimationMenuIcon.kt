package com.jamjamucho.belfry

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class AnimationMenuIcon(
    context: Context, attributeSet: AttributeSet)
    : AnimationIcon(R.drawable.ic_close, R.drawable.ic_menu, context, attributeSet) {

    override fun onProgressChanged(progress: Float, front: ImageView, back: ImageView) {
        front.rotation = -45f * progress
        back.rotation = 45f + front.rotation
        back.alpha = progress
        front.alpha = 1f - back.alpha
        front.scaleY = front.alpha
    }
}