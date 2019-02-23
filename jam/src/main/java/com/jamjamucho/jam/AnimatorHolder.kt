package com.jamjamucho.jam

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Build
import android.util.Log
import android.view.View
import com.jamjamucho.jam.inline.InlineAnimator
import com.jamjamucho.jam.internal.StateHolder
import com.jamjamucho.jam.internal.TriggerEvent
import java.lang.ref.WeakReference
import java.util.*

class AnimatorHolder
internal constructor(
    src: InlineAnimator,
    targetView: View?,
    private val sharedState: StateHolder?)
    : AnimatorHolderListener() {

    val id = src.id

    private val triggerId = src.trigger
    private val necessaryStateCondition = src.necessaryStateCondition
    private val nextState = src.nextState
    private val allowInterruption = src.allowInterruption
    private val repeatAtInterruption = src.repeatAtInterruption

    private val tag = AnimatorHolder::class.java.simpleName

    private val target = WeakReference(targetView)

    private val triggerEvents = EnumSet
        .noneOf(TriggerEvent::class.java)
        .apply {
            if (src.triggeredByClick) add(TriggerEvent.ON_CLICK)
            if (src.triggeredByLongClick) add(TriggerEvent.ON_LONG_CLICK)
        }

    private val animator =
        AnimatorInflater
            .loadAnimator(src.context, src.animator)
            .apply {
                addListener(this@AnimatorHolder)
                addPauseListener(this@AnimatorHolder)
            }

    internal fun fire(trigger: View, event: TriggerEvent) {
        if (checkStartConditions(trigger, event)) start()
    }

    fun start() {
        if (animationIsNotRunning()) onStart()
        else if (allowInterruption) onAnimationInterrupted()
    }

    fun reverse() {
        val animator = animator
        when (animator) {

            is ValueAnimator -> animator.reverse()

            is AnimatorSet ->
                if (26 <= Build.VERSION.SDK_INT) animator.reverse()
                else Log.e(tag, "Cannot reverse AnimatorSet because of low api level.")

            else ->
                throw IllegalArgumentException(
                    "Cannot reverse ${animator::class.java.name}")
        }
    }

    fun cancel() {
        animator.cancel()
    }

    fun addListener(listener: AnimatorHolderListener) {
        listener.registerTo(animator)
    }

    fun removeListener(listener: AnimatorHolderListener) {
        listener.unregisterFrom(animator)
    }

    private fun onAnimationInterrupted() {
        if (repeatAtInterruption) reverse()
        else onStart()
    }

    private fun onStart() {
        animator.apply {
            cancel()
            setTarget(target.get())
            start()
        }
    }

    private fun setNextState() {
        if (nextState != null) sharedState?.set(nextState)
    }

    private fun checkStartConditions(trigger: View, event: TriggerEvent)
            = checkTriggerCondition(trigger) &&
            checkEventCondition(event) &&
            checkStateCondition()

    private fun checkTriggerCondition(trigger: View)
            = trigger.id == triggerId

    private fun checkEventCondition(event: TriggerEvent)
            = triggerEvents.contains(event)

    private fun checkStateCondition()
            = necessaryStateCondition == null ||
            sharedState == null ||
            sharedState.isSameAs(necessaryStateCondition)

    private fun animationIsNotRunning()
            = !animator.isRunning

    override fun onAnimatorEnd(
        animator: Animator,
        isReverse: Boolean,
        isCancelled: Boolean) {
        animator.setTarget(null)
        if (!isCancelled && !isReverse) setNextState()
    }
}