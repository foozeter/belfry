package com.jamjamucho.pumpkin

import android.support.annotation.IdRes
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.util.Log
import android.view.View
import android.view.animation.Interpolator
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass

abstract class LayoutState {

    private val tag = LayoutState::class.java.name

    private val changesWithTargets =
        mutableListOf<Pair<(view: View) -> Unit, Set<Int>>>()

    private val transitions = mutableListOf<Transition>()

    internal fun getTargetIds()
            = changesWithTargets.flatMap { it.second }.toSet()

    internal fun performTransition(manager: LayoutStateManager) {

        val sceneRoot = manager.getLayout()
        sceneRoot ?: return
        val transition = merge(transitions)
        TransitionManager.beginDelayedTransition(sceneRoot, transition)
        changesWithTargets.forEach {
            val change = it.first
            it.second.forEach { id ->
                val view = manager.findViewBy(id)
                if (view != null) change(view)
            }
        }
    }

    // Use this method in init() method of a derived class.
    protected fun change(
        @IdRes vararg viewIds: Int,
        modifier: (view: View) -> Unit): TransitionConfig {
        changesWithTargets.add(modifier to viewIds.toSet())
        return TransitionConfig(viewIds)
    }

    private fun addTransition(transition: Transition) {
        val same = findSameTransition(sameAs = transition)
        if (same != null) same.targetIds.mergeWith(transition.targetIds)
        else transitions.add(transition)
    }

    private fun findSameTransition(sameAs: Transition) = transitions.find {
        it::class == sameAs::class &&
                it.duration == sameAs.duration &&
                it.interpolator == sameAs.interpolator
    }

    private fun instantiateTransitionOf(transitionClass: Class<out Transition>): Transition? {
        try {
            return transitionClass.getDeclaredConstructor().newInstance()

        } catch (e: Exception) {

            when {

                (e is IllegalAccessException) or
                        (e is NoSuchMethodException) ->
                    Log.w(tag, "${transitionClass.name} does not has a public empty constructor.")

                (e is InstantiationException) or
                        (e is InvocationTargetException) -> {
                    Log.w(tag, "Failed to instantiate ${transitionClass.name}")
                    e.printStackTrace()
                }
            }

            return null
        }
    }

    private fun merge(
        transitions: List<Transition>,
        setup: Transition.() -> Unit = {}) =
        if (transitions.size == 1) transitions[0].apply(setup)
        else TransitionSet().apply {
            transitions.forEach { addTransition(it) }
            ordering = TransitionSet.ORDERING_TOGETHER
        }.apply(setup)

    private fun <T> MutableList<T>.mergeWith(other: List<T>) {
        val merged = union(other)
        clear()
        addAll(merged)
    }

    /**
     * This class will be used as a return value of change() method.
     * Use this like:
     *
     * change(R.id.action_bar) {
     *   ...
     * }.withTransition(ChangeBounds::class) {
     *   duration = 400
     *   interpolator = LinearInterpolator()
     * }
     */
    inner class TransitionConfig(private val targets: IntArray) {

        var interpolator: Interpolator? = null
        var duration: Long? = null

        fun withTransition(
            transitionClass: KClass<out Transition>,
            configure: TransitionConfig.() -> Unit = {}) {

            configure(this)
            val transition = instantiateTransitionOf(transitionClass.java)
            targets.forEach { transition?.addTarget(it) }
            if (interpolator != null) transition?.interpolator = interpolator
            if (duration != null) transition?.duration = duration!!
            if (transition != null) addTransition(transition)
        }
    }
}