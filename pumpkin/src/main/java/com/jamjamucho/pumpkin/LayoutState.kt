package com.jamjamucho.pumpkin

import android.support.annotation.IdRes
import android.support.transition.*
import android.view.View
import android.view.animation.Interpolator
import com.jamjamucho.pumpkin.transition.*

abstract class LayoutState {

    companion object {
        private const val ILLEGAL_STATE_MESSAGE =
            "You cannot call LayoutState#change() method except in LayoutState#onMakeState()."
    }

    private var setupIsNotFinished = true

    private lateinit var parent: LayoutStateManager

    private val superChange = ChangeTogether()

    internal fun setup(parent: LayoutStateManager) {
        if (setupIsNotFinished) {
            this.parent = parent
            onMakeState()
            setupIsNotFinished = false
        }
    }

    // Call setup() method before call this.
    internal fun getTargetIds() = superChange.getTargetIds()

    // Call setup() method before call this.
    internal fun applyChanges() {
        val sceneRoot = parent.getLayout()
        val transition = superChange.getTransition()
        if (sceneRoot != null && transition != null)
            TransitionManager.beginDelayedTransition(sceneRoot, transition)
        applyChangesWithoutAnimation()
    }

    // Call setup() method before call this.
    internal fun applyChangesWithoutAnimation() {
        superChange.apply()
    }

    protected abstract fun onMakeState()

    protected fun changeTogether(modify: ChangeTogether.() -> Unit) =
        if (setupIsNotFinished) superChange.changeTogether(modify)
        else throw  IllegalStateException(ILLEGAL_STATE_MESSAGE)

    protected fun changeSequentially(modify: ChangeSequentially.() -> Unit) =
        if (setupIsNotFinished) superChange.changeSequentially(modify)
        else throw IllegalStateException(ILLEGAL_STATE_MESSAGE)

    private fun change(targetIds: Iterable<Int>, modify: (view: View) -> Unit) =
        if (setupIsNotFinished) superChange.change(targetIds, modify)
        else throw IllegalStateException(ILLEGAL_STATE_MESSAGE)

    protected fun change(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
            = change(targetIds.asIterable(), modify)

    protected fun changeFade(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
            = change(targetIds.asIterable(), modify).with(Fade())

    protected fun changeFadeIn(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
            = change(targetIds.asIterable(), modify).with(Fade(Fade.IN))

    protected fun changeFadeOut(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
            = change(targetIds.asIterable(), modify).with(Fade(Fade.OUT))

    protected fun changeBounds(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
            = change(targetIds.asIterable(), modify).with(ChangeBounds())

    protected fun changeX(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
            = change(targetIds.asIterable(), modify).with(ChangeX())

    protected fun changeY(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
            = change(targetIds.asIterable(), modify).with(ChangeY())

    protected fun changeTranslationX(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
            = change(targetIds.asIterable(), modify).with(ChangeTranslationX())

    protected fun changeTranslationY(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
            = change(targetIds.asIterable(), modify).with(ChangeTranslationY())

    protected fun changeAlpha(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
            = change(targetIds.asIterable(), modify).with(ChangeAlpha())

    abstract inner class IChange {
        internal abstract fun getTargetIds(): Iterable<Int>
        internal abstract fun getTransition(): Transition?
        internal abstract fun apply()
    }

    inner class Change(

        private val targetIds: Iterable<Int>,

        private val modify: (view: View) -> Unit): IChange() {

        private var transition: Transition? = null

        override fun getTargetIds() = targetIds

        override fun getTransition() = transition

        override fun apply() {
            targetIds.forEach {
                val view = parent.findViewBy(it)
                if (view != null) modify(view)
            }
        }

        fun with(transition: Transition,
                 configure: Transition.() -> Unit = {}): Change {
            this.transition = transition
            configure(transition)
            return this
        }

        fun after(doAfter: (view: View) -> Unit) {
            transition?.addListener(object: Transition.TransitionListener {
                override fun onTransitionResume(transition: Transition) {}
                override fun onTransitionPause(transition: Transition) {}
                override fun onTransitionCancel(transition: Transition) {}
                override fun onTransitionStart(transition: Transition) {}
                override fun onTransitionEnd(transition: Transition) {
                    targetIds.forEach {
                        val view = parent.findViewBy(it)
                        if (view != null) doAfter(view)
                    }
                }
            })
        }
    }

    abstract inner class Changes: IChange() {

        private val innerChanges = mutableListOf<IChange>()
        private val sharedTransitionConfig = TransitionConfig()

        final override fun getTargetIds() =
            innerChanges.flatMap { it.getTargetIds() }

        final override fun getTransition(): Transition? {
            val set = TransitionSet()
            set.ordering = getOrdering()
            innerChanges.forEach {
                val transition = it.getTransition()
                if (transition != null) set.addTransition(transition)
            }
            onApplySharedTransitionConfig(sharedTransitionConfig, set)
            return if (set.transitionCount != 0) set else null
        }

        final override fun apply() {
            innerChanges.forEach { it.apply() }
        }

        protected abstract fun getOrdering(): Int

        protected abstract fun onApplySharedTransitionConfig(
            config: TransitionConfig, transition: TransitionSet)

        fun change(@IdRes targetIds: Iterable<Int>,
                   modify: (view: View) -> Unit): Change {
            val change = Change(targetIds.toSet(), modify)
            innerChanges.add(change)
            return change
        }

        fun change(@IdRes vararg targetIds: Int,
                   modify: (view: View) -> Unit)
                = change(targetIds.asIterable(), modify)

        fun changeTogether(modify: ChangeTogether.() -> Unit): Changes {
            val change = ChangeTogether()
            modify(change)
            innerChanges.add(change)
            return change
        }

        fun changeSequentially(modify: ChangeSequentially.() -> Unit): Changes {
            val change = ChangeSequentially()
            modify(change)
            innerChanges.add(change)
            return change
        }

        fun shareTransitionConfig(configure: TransitionConfig.() -> Unit) {
            configure(sharedTransitionConfig)
        }

        fun changeFade(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
                = change(targetIds.asIterable(), modify).with(Fade())

        fun changeFadeIn(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
                = change(targetIds.asIterable(), modify).with(Fade(Fade.IN))

        fun changeFadeOut(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
                = change(targetIds.asIterable(), modify).with(Fade(Fade.OUT))

        fun changeBounds(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
                = change(targetIds.asIterable(), modify).with(ChangeBounds())

        fun changeX(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
                = change(targetIds.asIterable(), modify).with(ChangeX())

        fun changeY(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
                = change(targetIds.asIterable(), modify).with(ChangeY())

        fun changeTranslationX(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
                = change(targetIds.asIterable(), modify).with(ChangeTranslationX())

        fun changeTranslationY(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
                = change(targetIds.asIterable(), modify).with(ChangeTranslationY())

        fun changeAlpha(@IdRes vararg targetIds: Int, modify: (view: View) -> Unit)
                = change(targetIds.asIterable(), modify).with(ChangeAlpha())
    }

    inner class ChangeTogether: Changes() {

        override fun getOrdering() = TransitionSet.ORDERING_TOGETHER

        override fun onApplySharedTransitionConfig(
            config: TransitionConfig, transition: TransitionSet) {
            config.configure(transition)
        }
    }

    inner class ChangeSequentially: Changes() {

        override fun getOrdering() = TransitionSet.ORDERING_SEQUENTIAL

        override fun onApplySharedTransitionConfig(
            config: TransitionConfig, transition: TransitionSet) {
            for (i in 0 until transition.transitionCount) {
                config.configure(transition.getTransitionAt(i))
            }
        }
    }

    class TransitionConfig {

        var duration: Long? = null
        var interpolator: Interpolator? = null

        internal fun configure(transition: Transition) {
            if (duration != null) transition.duration = duration!!
            if (interpolator != null) transition.interpolator = interpolator!!
        }
    }
}