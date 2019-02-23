package com.jamjamucho.belfry.timer

open class TestMultiTimerCallback(
    private val tag: String): Timer.Callback {
    private val callbacks = mutableMapOf<Long, Timer.Callback>()

    constructor(): this(TestMultiTimerCallback::class.java.simpleName)

    override fun onStart(timerID: Long) {
        val callback = TestTimerCallback(tag)
        callbacks[timerID] = callback
        callback.onStart(timerID)
    }

    override fun onPause(timerID: Long) {
        callbacks[timerID]?.onPause(timerID)
    }

    override fun onResume(timerID: Long) {
        callbacks[timerID]?.onResume(timerID)
    }

    override fun onCancelled(timerID: Long) {
        callbacks[timerID]?.onCancelled(timerID)
    }

    override fun onFinish(timerID: Long) {
        val callback = callbacks.remove(timerID)
        callback?.onFinish(timerID)
    }

    override fun onTick(timerID: Long, left: Long) {
        callbacks[timerID]?.onTick(timerID, left)
    }
}