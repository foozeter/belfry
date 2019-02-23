package com.jamjamucho.belfry.timer

import android.os.Handler
import android.os.SystemClock

class Timer(
    private val quartz: Quartz
): Quartz.OnOscillateListener {
    private val handler = Handler()
    private var startTime = 0L
    private var endTime = 0L
    private var nextTickTime = 0L
    private var savedLeftTime = 0L
    private var callback: Callback? = null

    var started = false; private set
    var isWorking = false; private set
    var id = 0L

    init {
        quartz.addOnOscillateListener(this)
    }

    fun scrap() {
        stop()
        quartz.removeOnOscillateListener(this)
    }

    fun finalize() {
        scrap()
    }

    fun start(millisInFuture: Long) {
        if (quartz.isAlreadyActivated) when {

                started -> return

                0 < millisInFuture -> {
                    prepare(millisInFuture)
                    started = true
                    isWorking = true
                    callback?.onStart(id)
                }

                else -> {
                    callback?.onFinish(id)
                }

        } else throw IllegalStateException(
            "The quartz must be activated at this time.")
    }

    fun pause() {
        if (started && isWorking) {
            savedLeftTime = rapLeft()
            isWorking = false
            callback?.onPause(id)
        }
    }

    fun resume() {
        if (started && !isWorking) {
            prepare(savedLeftTime)
            isWorking = true
            callback?.onResume(id)
        }
    }

    fun cancel() {
        if (started) {
            stop()
            callback?.onCancelled(id)
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setCallback(config: CallbackBuilder.() -> Unit)
            = setCallback(CallbackBuilder().apply(config).build())

    fun removeCallback() {
        callback = null
    }

    override fun onOscillate() {
        if (isWorking) {
            val left = rapLeft()
            if (quartz.period < left) {
                postDelayed(nextTickTime - rap()) {
                    // If the timer is paused at this time,
                    // this post will be ignored.
                    if (isWorking) callback?.onTick(id, rapLeft())
                }
                nextTickTime += quartz.period
            } else {
                stop()
                postDelayed(left) {
                    callback?.onFinish(id)
                }
            }
        }
    }

    private fun prepare(millisInFuture: Long) {
        startTime = rap()
        endTime = startTime + millisInFuture
        nextTickTime = startTime + quartz.period
    }

    private fun rap() = SystemClock.elapsedRealtime()

    private fun rapLeft() = endTime - rap()

    private fun stop() {
        started = false
        isWorking = false
    }

    private fun postDelayed(delay: Long, runnable: () -> Unit) {
        handler.postDelayed(runnable, delay)
    }

    interface Callback {
        fun onStart(timerID: Long)
        fun onPause(timerID: Long)
        fun onResume(timerID: Long)
        fun onCancelled(timerID: Long)
        fun onFinish(timerID: Long)
        fun onTick(timerID: Long, left: Long)
    }

    class CallbackBuilder {

        private var onStartListener = {_: Long -> }
        private var onPauseListener = {_: Long -> }
        private var onResumeListener = {_: Long -> }
        private var onCancelledListener = {_: Long -> }
        private var onFinishListener = {_: Long -> }
        private var onTickListener = {_: Long, _: Long -> }

        fun onStart(listener: (timerID: Long) -> Unit) {
            onStartListener = listener
        }

        fun onPause(listener: (timerID: Long) -> Unit) {
            onPauseListener = listener
        }

        fun onResume(listener: (timerID: Long) -> Unit) {
            onResumeListener = listener
        }

        fun onCancelled(listener: (timerID: Long) -> Unit) {
            onCancelledListener = listener
        }

        fun onFinish(listener: (timerID: Long) -> Unit) {
            onFinishListener = listener
        }

        fun onTick(listener: (timerID: Long, left: Long) -> Unit) {
            onTickListener = listener
        }

        fun build() = object: Callback {
            override fun onStart(timerID: Long) = onStartListener(timerID)
            override fun onPause(timerID: Long) = onPauseListener(timerID)
            override fun onResume(timerID: Long) = onResumeListener(timerID)
            override fun onCancelled(timerID: Long) = onCancelledListener(timerID)
            override fun onFinish(timerID: Long) = onFinishListener(timerID)
            override fun onTick(timerID: Long, left: Long) = onTickListener(timerID, left)
        }
    }
}