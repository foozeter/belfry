package com.jamjamucho.jam.inline

import android.content.Context
import android.support.annotation.AnimatorRes
import android.support.annotation.IdRes
import android.util.AttributeSet
import android.view.InflateException
import com.jamjamucho.jam.R

class InlineAnimator(
    context: Context, attributeSet: AttributeSet)
    : BaseInlineAnimator(context, attributeSet) {

    @IdRes
    var target: Int

    @AnimatorRes
    var animator: Int

    init {

        val attrs = context.obtainStyledAttributes(
            attributeSet, R.styleable.InlineAnimator, 0, 0)

        var id = attrs.getResourceId(R.styleable.InlineAnimator_jam_target, 0)
        target = if (id > 0) id else throw InflateException("Attribute 'jam_target' not found.")

        id = attrs.getResourceId(R.styleable.InlineAnimator_jam_animator, 0)
        animator = if (id > 0) id else throw InflateException("Attribute 'jam_animatorSrc' not found.")

        attrs.recycle()
    }
}