package com.jamjamucho.jam.internal

import android.view.View
import android.view.ViewGroup
import com.jamjamucho.jam.Jam
import com.jamjamucho.jam.inline.Animation
import com.jamjamucho.jam.inline.Config
import com.jamjamucho.jam.inline.Trigger
import java.util.*

internal class Builder {

    fun createFrom(layout: View): Jam {

        val targetViews = mutableListOf<View>()
        val triggerViews = mutableListOf<View>()
//        val animators = mutableListOf<InlineAnimator>()
        val animations = mutableListOf<Animation>()
        val triggers = mutableListOf<Trigger>()
        val configs = mutableListOf<Config>()

        // Collect InlineAnimators
        layout.scan { child, recycleBin ->
            when (child) {

                is Config -> {
                    configs.add(child)
                    recycleBin.add(child)
                }

//                is InlineAnimator -> {
//                    animators.add(child)
//                    recycleBin.add(child)
//                }

//                is InlineAnimatorGroup -> {
//                    animators.addAll(child.obtainChildren())
//                    recycleBin.add(child)
//                }
//
                is Animation -> {
                    animations.add(child)
                    recycleBin.add(child)
                }

                is Trigger -> {
                    triggers.add(child)
                    recycleBin.add(child)
                }
            }
        }

        val targetIds = animations
            .map { it.target }
            .toMutableList()

        val triggerIds = triggers
            .mapNotNull { it.trigger }
            .toMutableList()

        // Collect target and trigger views
        layout.fullScan {

            if (targetIds.contains(it.id)) {
                targetViews.add(it)
                targetIds.remove(it.id)
            }

            if (triggerIds.contains(it.id)) {
                triggerViews.add(it)
                triggerIds.remove(it.id)
            }

            // Continue until all targetID views and triggerID views is found
            return@fullScan targetIds.isEmpty() && triggerIds.isEmpty()
        }

        return Jam(triggerViews, targetViews, triggers, animations, configs)
    }

    private fun View.scan(onChild: (child: View, recycleBin: MutableList<View>) -> Unit) {
        if (this !is ViewGroup) return
        val recycleBin = mutableListOf<View>()
        for (i in 0 until childCount) {
            onChild(getChildAt(i), recycleBin)
        }
        recycleBin.forEach { removeView(it) }
    }

    private fun View.fullScan(onChild: (view: View) -> Boolean) {
        // Breadth first search
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
}