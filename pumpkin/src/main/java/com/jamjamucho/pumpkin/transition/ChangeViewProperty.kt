package com.jamjamucho.pumpkin.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.support.transition.Transition
import android.support.transition.TransitionValues
import android.view.View
import android.view.ViewGroup

abstract class ChangeViewProperty: Transition() {

    private val tag = ChangeViewProperty::class.java.name

    private val PROP_FLOAT by lazy { "$tag:$propertyName" }

    protected abstract val propertyName: String

    final override fun captureStartValues(transitionValues: TransitionValues)
            = captureValues(transitionValues)

    final override fun captureEndValues(transitionValues: TransitionValues)
            = captureValues(transitionValues)

    private fun captureValues(transitionValues: TransitionValues) {
        transitionValues.values[PROP_FLOAT] = onCaptureValue(transitionValues.view)
    }

    protected abstract fun onCaptureValue(view: View): Float

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?): Animator? {
        startValues ?: return null
        endValues ?: return null
        val start = startValues.values[PROP_FLOAT] as? Float
        val end = endValues.values[PROP_FLOAT] as? Float
        start ?: return null
        end ?: return null
        val view = startValues.view
        return ObjectAnimator.ofFloat(view, propertyName, start, end)
    }
}