package com.jamjamucho.jam

import android.animation.AnimatorInflater
import android.view.View
import com.jamjamucho.jam.inline.Animation
import java.lang.ref.WeakReference

class AnimationHolder(src: Animation, target: View?) {

    val name = src.name

    private val animator = AnimatorInflater.loadAnimator(src.context, src.src)

    private val targetRef = WeakReference(target)

    private val allowInterruption = src.allowInterruption

    private val repeatAtInterruption = src.repeatAtInterruption

    fun start() {}

    fun reverse() {}
}