package com.jamjamucho.smile

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import com.jamjamucho.smile.parse.AnimationsBean
import com.jamjamucho.smile.parse.BindingBean

class AnimatorHolder(
    context: Context,
    src: AnimationsBean,
    allTargetViews: Map<Int, View>,
    allBindings: Map<Int, BindingBean>)
    : Animator.AnimatorListener {

    var isCancelled = false; private set

    var isReversed = false; private set

    val isNotCancelled; get() = !isCancelled

    val isNotReversed; get() = !isReversed

    internal val id = src.id

    private val tag = AnimatorHolder::class.java.name

    private val animator = AnimatorSet().apply {
        addListener(this@AnimatorHolder)
        playTogether(
            src.refs
                .mapNotNull { allBindings[it.binding] }
                .map {
                    AnimatorInflater
                        .loadAnimator(context, it.animator)
                        .apply { setTarget(allTargetViews[it.target]) }
                })
    }

    private val allowInterruption = src.allowInterruption

    private val reverseAtInterruption = src.allowInterruption

    private val callbacks = mutableListOf<AnimatorCallback>()

    override fun onAnimationRepeat(animation: Animator) {
        callbacks.forEach { it.onAnimationRepeat(this) }
    }

    override fun onAnimationCancel(animation: Animator) {
        isCancelled = true
        callbacks.forEach { it.onAnimationCancel(this) }
    }

    override fun onAnimationEnd(animation: Animator) {
        onAnimationEnd(animation, false)
    }

    override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
        this.isReversed = isReverse
        callbacks.forEach { it.onAnimationEnd(this) }
    }

    override fun onAnimationStart(animation: Animator) {
        onAnimationStart(animation, false)
    }

    override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
        isCancelled = false
        this.isReversed = isReverse
        callbacks.forEach { it.onAnimationStart(this) }
    }

    fun addCallback(callback: AnimatorCallback) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: AnimatorCallback) {
        callbacks.remove(callback)
    }

    fun start() {
        if (animationIsNotRunning()) onStart()
        else if (allowInterruption) onAnimationInterrupted()
    }

    fun reverse() {
        if (26 <= Build.VERSION.SDK_INT) animator.reverse()
        else Log.e(tag, "Cannot reverse AnimatorSet because of low api level.")
    }

    fun cancel() {
        animator.cancel()
    }


    private fun onAnimationInterrupted() {
        if (reverseAtInterruption) reverse()
        else onStart()
    }

    private fun onStart() {
        cancel()
        animator.start()
    }

    private fun animationIsNotRunning()
            = !animator.isRunning
}