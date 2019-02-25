package com.jamjamucho.smile

import android.util.Log
import com.jamjamucho.smile.parse.StateBean
import com.jamjamucho.smile.parse.StatesBean

internal class StateList(src: List<StatesBean>): AnimatorCallback() {

    // key = AnimatorHolder.id
    private val postedStates = mutableMapOf<Int, PostedState>()

    private val nullState = object: State(0) {
        override fun isSameAs(value: Int?) = false
    }

    private val states = src
        .flatten()
        .associate { it.name to State(it.default) }

    operator fun get(name: Int?)
            = states[name] ?: nullState

    override fun onAnimationEnd(animation: AnimatorHolder) {
        Log.d("mylog", "onAnimationEnd")
        if (animation.isNotCancelled && animation.isNotReversed) {
            postedStates.remove(animation.id)?.push()
        }
    }

    private fun List<StatesBean>.flatten()
            = mutableListOf<StateBean>().apply {
        this@flatten.forEach { addAll(it.states) }
    }

    private class PostedState(
        val futureState: Int,
        val linkedState: State) {

        fun push() {
            linkedState.set(futureState)
        }
    }

    open inner class State(private var value: Int) {

        open fun isSameAs(value: Int?)
                = value == this.value

        fun set(value: Int) {
            this.value = value
        }

        fun postFutureState(value: Int?, postedBy: Trigger) {
            if (value != null) {
                postedStates[postedBy.animation] = PostedState(value, this)
            }
        }
    }
}