package com.jamjamucho.pumpkin

import android.view.View
import android.view.ViewGroup
import java.lang.ref.WeakReference
import java.util.*
import kotlin.reflect.KClass

class LayoutStateManager private constructor(

    layout: ViewGroup,

    private val states: Map<KClass<out LayoutState>, LayoutState>) {

    private val layout = WeakReference(layout)

    private val targets = mutableMapOf<Int, WeakReference<View>>()
        .apply {

            val targetIds = states.values
                .flatMap { it.getTargetIds() }
                .toMutableSet()

            // Collect target views.
            layout.breadthFirstSearch {
                if (targetIds.contains(it.id)) {
                    put(it.id, WeakReference(it))
                    targetIds.remove(it.id)
                }
                return@breadthFirstSearch targetIds.isEmpty()
            }
        }
        .toMap()

    fun go(state: KClass<out LayoutState>) {
        states[state]?.performTransition(this)
    }

    internal fun findViewBy(id: Int) = targets[id]?.get()

    internal fun getLayout() = layout.get()

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

    class Builder internal constructor(

        private val layout: ViewGroup) {

        private val states = mutableMapOf<KClass<out LayoutState>, LayoutState>()

        fun addState(state: LayoutState): Builder {
            states[state::class] = state
            return this
        }

        fun build() = LayoutStateManager(layout, states)
    }

    companion object {

        fun setupWith(layout: ViewGroup) = Builder(layout)
    }
}