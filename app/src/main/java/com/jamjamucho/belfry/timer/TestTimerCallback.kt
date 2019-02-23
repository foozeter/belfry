package com.jamjamucho.belfry.timer

import android.os.SystemClock
import android.util.Log

open class TestTimerCallback(
    private val tag: String) : Timer.Callback {
    private var startTime = 0L
    private var prevTickTime = 0L
    private var pausedTime = 0L
    private var totalPausedTime = 0L

    constructor() : this(TestTimerCallback::class.java.simpleName)

    override fun onPause(timerID: Long) {
        pausedTime = SystemClock.elapsedRealtime()
        Log.d(tag, "onPause(id=$timerID)")
    }

    override fun onResume(timerID: Long) {
        totalPausedTime += SystemClock.elapsedRealtime() - pausedTime
        Log.d(tag, "onResume(id=$timerID)")
    }

    override fun onCancelled(timerID: Long) {
        Log.d(tag, "onCancelled(id=$timerID)")
    }

    override fun onStart(timerID: Long) {
        startTime = SystemClock.elapsedRealtime()
        prevTickTime = startTime
        Log.d(tag, "onCreateAnimator(id=$timerID)")
    }

    override fun onTick(timerID: Long, left: Long) {
        val now = SystemClock.elapsedRealtime()
        Log.d(tag, "onTick(id=$timerID, left = ${left}ms); interval=${now - prevTickTime}ms")
        prevTickTime = now
    }

    override fun onFinish(timerID: Long) {
        val realElapsedTime = SystemClock.elapsedRealtime() - startTime
        val elapsedTime = realElapsedTime - totalPausedTime
        Log.d(tag, "onFinish(id=$timerID); " +
                "realElapsedTime=${realElapsedTime}ms, " +
                "totalPausedTime=${totalPausedTime}ms, " +
                "elapsedTime=${elapsedTime}ms")
        reset()
    }

    private fun reset() {
        startTime = 0L
        pausedTime = 0L
        totalPausedTime = 0L
        prevTickTime = 0L
    }
}