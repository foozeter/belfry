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

    companion object {
        private const val TAG = "LayoutState"
    }

    private val transitionConfigs = mutableListOf<TransitionConfig>()
    private val transitions = mutableListOf<Transition>()
    private val changes = mutableListOf<BaseChange>()

    internal fun setup() {
        transitionConfigs.clear()
        transitions.clear()
        changes.clear()
        // Use change() or changes() methods in onMakeState() method
        // to create objects of Change, Changes or TransitionConfig.
        onMakeState()
        transitionConfigs
            .compress { a, b -> a.mergeWith(b) }
            .forEach { transitions.addIfNotNull(it.makeConfiguredTransition()) }
        transitionConfigs.clear()
    }

    // Call setup() method before call this.
    internal fun getTargetIds()
            = changes.flatMap { it.targetIds }.toSet()

    // Call setup() method before call this.
    internal fun applyChanges(manager: LayoutStateManager) {
        val sceneRoot = manager.getLayout()
        if (sceneRoot != null) {
            val transition = merge(transitions)
            TransitionManager.beginDelayedTransition(sceneRoot, transition)
            applyChangesWithoutAnimation(manager)
        }
    }

    // Call setup() method before call this.
    internal fun applyChangesWithoutAnimation(manager: LayoutStateManager) {
        changes.forEach { it.apply(manager) }
    }

    protected abstract fun onMakeState()

    protected fun change(
        @IdRes vararg targetIds: Int,
        modify: (view: View) -> Unit): Change {
        val change = Change(targetIds.toSet(), modify)
        changes.add(change)
        return change
    }

    protected fun changes(modify: Changes.() -> Unit): Changes {
        val change = Changes()
        modify(change)
        changes.add(change)
        return change
    }

    private fun merge(
        transitions: List<Transition>,
        setup: Transition.() -> Unit = {}) =
        if (transitions.size == 1) transitions[0].apply(setup)
        else TransitionSet().apply {
            transitions.forEach { addTransition(it) }
            ordering = TransitionSet.ORDERING_TOGETHER
        }.apply(setup)

    private fun <T> MutableList<T>.compress(
        merge: (a: T, b: T) -> T?): MutableList<T> {

        var start = size - 1
        while (0 < start) {
            var index = start - 1
            while (0 <= index) {
                val merged = merge(get(start), get(index))
                if (merged != null) {
                    set(start, merged)
                    removeAt(index)
                    --start
                }
                --index
            }
            --start
        }

        return this
    }

    private fun <T> MutableList<T>.addIfNotNull(value: T?) {
        if (value != null) add(value)
    }

    abstract inner class BaseChange {

        internal abstract val targetIds: Set<Int>

        fun withTransition(
            type: KClass<out Transition>,
            configure: TransitionConfig.() -> Unit) {
            val config = TransitionConfig(type, targetIds)
            configure(config)
            transitionConfigs.add(config)
        }

        internal abstract fun apply(manager: LayoutStateManager)
    }

    inner class Change(
        override val targetIds: Set<Int>,
        private val modify: (view: View) -> Unit): BaseChange() {

        override fun apply(manager: LayoutStateManager) {
            targetIds.forEach {
                val view = manager.findViewBy(it)
                if (view != null) modify(view)
            }
        }
    }

    inner class Changes: BaseChange() {

        override val targetIds: Set<Int>
            get() = innerChanges.flatMap { it.targetIds }.toSet()

        private val innerChanges = mutableListOf<BaseChange>()

        override fun apply(manager: LayoutStateManager) {
            innerChanges.forEach { it.apply(manager) }
        }

        fun change(
            @IdRes vararg targetIds: Int,
            modify: (view: View) -> Unit): Change {
            val change = Change(targetIds.toSet(), modify)
            innerChanges.add(change)
            return change
        }

        fun changes(modify: Changes.() -> Unit): Changes {
            val change = Changes()
            modify(change)
            innerChanges.add(change)
            return change
        }
    }

    class TransitionConfig(

        internal val type: KClass<out Transition>,

        internal val targetIds: Set<Int>) {

        var interpolator: Interpolator? = null

        var duration: Long? = null

        internal fun mergeWith(other: TransitionConfig) =
            if (isSameAs(other)) {
                TransitionConfig(type, targetIds.union(other.targetIds)).apply {
                    duration = this@TransitionConfig.duration
                    interpolator = this@TransitionConfig.interpolator
                }
            } else null

        internal fun makeConfiguredTransition() =
            instantiateTransitionOf(type.java)?.apply {
                if (this@TransitionConfig.interpolator != null) interpolator = this@TransitionConfig.interpolator
                if (this@TransitionConfig.duration != null) duration = this@TransitionConfig.duration!!
            }

        private fun isSameAs(other: TransitionConfig)
                = type == other.type
                && interpolator == other.interpolator
                && duration == other.duration

        private fun instantiateTransitionOf(transitionClass: Class<out Transition>): Transition? {
            try {
                return transitionClass.getDeclaredConstructor().newInstance()

            } catch (e: Exception) {

                when {

                    (e is IllegalAccessException) or
                            (e is NoSuchMethodException) ->
                        Log.w(TAG, "${transitionClass.name} does not has a public empty constructor.")

                    (e is InstantiationException) or
                            (e is InvocationTargetException) -> {
                        Log.w(TAG, "Failed to instantiate ${transitionClass.name}")
                        e.printStackTrace()
                    }
                }

                return null
            }
        }
    }
}