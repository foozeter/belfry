package com.jamjamucho.smile

import android.support.annotation.XmlRes
import android.view.View
import android.view.ViewGroup
import com.jamjamucho.smile.parse.AnimatorAssociatingInflater
import java.util.*

class Smile(@XmlRes src: Int, layout: View) {

    internal val states: StateList

    internal val animations: Map<Int, AnimatorHolder>

    private val triggers: List<Trigger>

    // A key is the id of a trigger view
    private val clickObservers: Map<Int, OnClickObserver>
    private val longClickObservers: Map<Int, OnLongClickObserver>

    init {

        val animatorAssociating =
            AnimatorAssociatingInflater(layout.context).inflate(src)

        val targetIds =
            animatorAssociating
                .bindings
                .values
                .map { it.target }
                .toMutableList()

        val triggerIds =
            animatorAssociating
                .triggers
                .map { it.trigger }
                .toMutableList()

        val targetViews = mutableMapOf<Int, View>()
        val triggerViews = mutableListOf<View>()

        // Collect target and trigger views
        layout.breadthFirstSearch {

            if (targetIds.contains(it.id)) {
                targetViews[it.id] = it
                targetIds.remove(it.id)
            }

            if (triggerIds.contains(it.id)) {
                triggerViews.add(it)
                triggerIds.remove(it.id)
            }

            // Continue until all targetID views and triggerID views is found
            return@breadthFirstSearch targetIds.isEmpty() && triggerIds.isEmpty()
        }

        clickObservers = triggerViews.associate {
            it.id to OnClickObserver().apply { it.setOnClickListener(this) }
        }

        longClickObservers = triggerViews.associate {
            it.id to OnLongClickObserver().apply { it.setOnLongClickListener(this) }
        }

        animations = animatorAssociating
            .animations
            .mapValues {
                AnimatorHolder(
                    layout.context,
                    it.value,
                    targetViews,
                    animatorAssociating.bindings)
            }

        triggers = animatorAssociating
            .triggers
            .map { Trigger(it) }

        states = StateList(animatorAssociating.states)
        animations.values.forEach { it.addCallback(states) }
    }

    private fun onEventOccurred(trigger: View, event: TriggerEvent) {
        triggers.forEach { it.fire(trigger, event, this) }
    }

    private fun View.breadthFirstSearch(onChild: (view: View) -> Boolean) {
        val queue = ArrayDeque<View>()
        queue.add(this)
        while (queue.isNotEmpty()) {
            val child = queue.poll()
            val finish = onChild(child)
            if (finish) return
            if (child is ViewGroup) child.addChildrenInto(queue)
        }
    }

    private fun ViewGroup.addChildrenInto(queue: ArrayDeque<View>) {
        for (i in 0 until childCount) queue.add(getChildAt(i))
    }

    private inner class OnClickObserver: View.OnClickListener {

        var additionalListener: View.OnClickListener? = null

        override fun onClick(view: View) {
            additionalListener?.onClick(view)
            onEventOccurred(view, TriggerEvent.ON_CLICK)
        }
    }

    private inner class OnLongClickObserver: View.OnLongClickListener {

        var additionalListener: View.OnLongClickListener? = null

        override fun onLongClick(view: View): Boolean {
            additionalListener?.onLongClick(view)
            onEventOccurred(view, TriggerEvent.ON_LONG_CLICK)
            return true
        }
    }
}