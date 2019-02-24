package com.jamjamucho.jam.inline

import android.content.Context
import android.support.annotation.AnimatorRes
import android.support.annotation.IdRes
import android.util.AttributeSet
import android.view.InflateException
import com.jamjamucho.jam.R

class Animation(
    context: Context, attributeSet: AttributeSet)
    : BaseInline(context, attributeSet) {

    @IdRes
    val target: Int

    @AnimatorRes
    val src: Int

    val name: String?

    val allowInterruption: Boolean

    val repeatAtInterruption: Boolean

    init {

        val attrs = context.obtainStyledAttributes(attributeSet, R.styleable.Animation, 0, 0)

        var id = attrs.getResourceId(R.styleable.Animation_jam_target, 0)
        target = if (id > 0) id else throw InflateException("Attribute 'jam_target' not found.")

        id = attrs.getResourceId(R.styleable.Animation_jam_src, 0)
        src = if (id > 0) id else throw InflateException("Attribute 'jam_animatorSrc' not found.")

        name = attrs.getString(R.styleable.Animation_jam_name)

        allowInterruption = attrs.getBoolean(
            R.styleable.Animation_jam_allowInterruption, true)

        repeatAtInterruption = attrs.getBoolean(
            R.styleable.Animation_jam_reverseAtInterruption, false)

        attrs.recycle()
    }
}