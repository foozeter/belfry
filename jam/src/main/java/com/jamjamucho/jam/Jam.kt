package com.jamjamucho.jam

import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import com.jamjamucho.jam.helper.StateListLoader
import com.jamjamucho.jam.inline.Animation
import com.jamjamucho.jam.inline.Config
import com.jamjamucho.jam.inline.Trigger
import com.jamjamucho.jam.internal.Builder
import com.jamjamucho.jam.internal.Constant
import com.jamjamucho.jam.internal.StateHolder
import com.jamjamucho.jam.internal.TriggerEvent

class Jam internal constructor(
    triggerViews: List<View>,
    targetViews: List<View>,
    triggers: List<Trigger>,
    animations: List<Animation>,
    configs: List<Config>) {

//    private val animators = mutableListOf<AnimatorHolder>()
    private val clickObservers = mutableMapOf<Int, OnClickObserver>()
    private val longClickObservers = mutableMapOf<Int, OnLongClickObserver>()

    private val animations: List<AnimationHolder>
    private val triggers: List<TriggerHolder>

    init {

        val defaultStates = mutableMapOf<String, String>().apply {
            configs.forEach { putAll(obtainDefaultStatesFrom(it)) }
        }

        val sharedStates = triggers
            .mapNotNull { it.stateName }
            .distinct()
            .associate { it to StateHolder(defaultStates[it] ?: Constant.DEFAULT_STATE) }

//        animatorViews.forEach {
//            animators.add(AnimatorHolder(
//                it,
//                targets[it.target],
//                sharedStates[it.stateName]))
//        }

        val targets = targetViews.associateBy { it.id }

        this.animations = animations.map { AnimationHolder(it, targets[it.target]) }

        this.triggers = triggers.map { TriggerHolder(it, this, sharedStates[it.stateName]) }

        triggerViews.forEach {
            val clickObserver = OnClickObserver()
            val longClickObserver = OnLongClickObserver()
            it.setOnClickListener(clickObserver)
            it.setOnLongClickListener(longClickObserver)
            clickObservers[it.id] = clickObserver
            longClickObservers[it.id] = longClickObserver
        }
    }

    fun setTriggerViewOnClickListener(triggerID: Int, listener: View.OnClickListener) {
        clickObservers[triggerID]?.additionalListener = listener
    }

    fun setTriggerViewOnClickListener(triggerID: Int, listener: (view: View) -> Unit)
            = setTriggerViewOnClickListener(triggerID, onClickListener(listener))

    fun removeTriggerViewOnClickListener(triggerID: Int) {
        clickObservers[triggerID]?.additionalListener = null
    }

    fun setTriggerViewOnLongClickListener(triggerID: Int, listener: View.OnLongClickListener) {
        longClickObservers[triggerID]?.additionalListener = listener
    }

    fun setTriggerViewOnLongClickListener(triggerID: Int, listener: (view: View) -> Unit)
            = setTriggerViewOnLongClickListener(triggerID, onLongClickListener(listener))

    fun removeTriggerViewOnLongClickListener(triggerID: Int) {
        longClickObservers[triggerID]?.additionalListener = null
    }

    fun addAnimatorListener(animatorId: Int, listener: AnimatorHolderListener) {
//        animators.find { it.id == animatorId }?.addListener(listener)
    }

    fun removeAnimatorListener(animatorId: Int, listener: AnimatorHolderListener) {
//        animators.find { it.id == animatorId }?.removeListener(listener)
    }

//    fun findAnimator(animatorId: Int) = animators.find { it.id == animatorId }

    internal fun findAnimations(name: String)
            = animations.filter { it.name == name }

    private fun onClickListener(listener: (view: View) -> Unit)
            = object: View.OnClickListener {
        override fun onClick(view: View) = listener(view)
    }

    private fun onLongClickListener(listener: (view: View) -> Unit)
            = object: View.OnLongClickListener {
        override fun onLongClick(view: View): Boolean {
            listener(view)
            return true
        }
    }

    private fun obtainDefaultStatesFrom(config: Config?): Map<String, String> =
        if (config?.defaultStateList == null) emptyMap()
        else StateListLoader().obtain(config.context, config.defaultStateList)

    private inner class OnClickObserver: View.OnClickListener {

        var additionalListener: View.OnClickListener? = null

        override fun onClick(view: View) {
            additionalListener?.onClick(view)
//            animators.forEach { it.fire(view, TriggerEvent.ON_CLICK) }
            triggers.forEach { it.fire(view, TriggerEvent.ON_CLICK) }
        }
    }

    private inner class OnLongClickObserver: View.OnLongClickListener {

        var additionalListener: View.OnLongClickListener? = null

        override fun onLongClick(view: View): Boolean {
            additionalListener?.onLongClick(view)
//            animators.forEach { it.fire(view, TriggerEvent.ON_LONG_CLICK) }
            triggers.forEach { it.fire(view, TriggerEvent.ON_LONG_CLICK) }
            return true
        }
    }

    companion object {

        fun setup(layout: View)
                = Builder().createFrom(layout)

        fun setup(activity: AppCompatActivity, layout: Int): Jam {
            activity.setContentView(layout)
            val rootView = activity
                .findViewById<ViewGroup>(android.R.id.content)
                .getChildAt(0)
            return setup(rootView)
        }
    }
}