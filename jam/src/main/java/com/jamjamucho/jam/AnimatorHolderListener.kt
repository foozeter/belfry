package com.jamjamucho.jam

import android.animation.Animator

abstract class AnimatorHolderListener
    : Animator.AnimatorListener, 
    Animator.AnimatorPauseListener {

    private var isCancelled = false

    fun registerTo(animator: Animator) {
        animator.addListener(this)
        animator.addPauseListener(this)
    }

    fun unregisterFrom(animator: Animator) {
        animator.removeListener(this)
        animator.removePauseListener(this)
    }

    final override fun onAnimationRepeat(animation: Animator)
            = onAnimatorResume(animation)

    final override fun onAnimationPause(animation: Animator)
            = onAnimatorPause(animation)

    final override fun onAnimationResume(animation: Animator)
            = onAnimatorResume(animation)

    final override fun onAnimationCancel(animation: Animator) {
        isCancelled = true
        onAnimatorCancel(animation)
    }

    final override fun onAnimationEnd(animation: Animator, isReverse: Boolean)
            = onAnimatorEnd(animation, isReverse, isCancelled)

    final override fun onAnimationEnd(animation: Animator)
            = onAnimationEnd(animation, false)

    final override fun onAnimationStart(animation: Animator)
            = onAnimationStart(animation, false)

    final override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
        isCancelled = false
        onAnimatorStart(animation, isReverse)
    }

    open fun onAnimatorEnd(animator: Animator,isReverse: Boolean, isCancelled: Boolean) {}
    open fun onAnimatorStart(animator: Animator, isReverse: Boolean) {}
    open fun onAnimatorPause(animator: Animator) {}
    open fun onAnimatorResume(animator: Animator) {}
    open fun onAnimatorCancel(animator: Animator) {}
}