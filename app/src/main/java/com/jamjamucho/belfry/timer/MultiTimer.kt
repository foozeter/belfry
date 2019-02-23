package com.jamjamucho.belfry.timer

class MultiTimer(tickPeriod: Long) {

    private var callback: Timer.Callback? = null
    private val timers = IDMap<Timer>()
    private val quartz = Quartz(tickPeriod)

    private val callbackDispatcher = object: Timer.Callback {
        override fun onStart(timerID: Long) = callback?.onStart(timerID) ?: Unit
        override fun onPause(timerID: Long) = callback?.onPause(timerID) ?: Unit
        override fun onResume(timerID: Long) = callback?.onResume(timerID) ?: Unit
        override fun onCancelled(timerID: Long) = callback?.onCancelled(timerID) ?: Unit
        override fun onFinish(timerID: Long) = callback?.onFinish(timerID) ?: Unit
        override fun onTick(timerID: Long, left: Long) = callback?.onTick(timerID, left) ?: Unit
    }

    init {
        quartz.activate()
    }

    fun finalize() {
        quartz.quit()
    }

    fun setCallback(callback: Timer.Callback) {
        this.callback = callback
    }

    fun setCallback(config: Timer.CallbackBuilder.() -> Unit)
            = setCallback(Timer.CallbackBuilder().apply(config).build())

    fun removeCallback() {
        callback = null
    }

    fun start(millisInFuture: Long): Long {
        val timer = Timer(quartz)
        timer.setCallback(callbackDispatcher)
        timer.id = timers.put(timer)
        timer.start(millisInFuture)
        return timer.id
    }

    fun pause(timerID: Long) {
        timers[timerID]?.pause()
    }

    fun resume(timerID: Long) {
        timers[timerID]?.resume()
    }

    fun cancel(timerID: Long) {
        timers[timerID]?.cancel()
    }

    fun scrap(timerID: Long) {
        timers.remove(timerID)?.scrap()
    }

    fun scrap() {
        quartz.quit()
        timers.clear()
        removeCallback()
    }

    fun hasStartedTimers() = timers.has { it.started }

    fun hasWorkingTimers() = timers.has { it.isWorking }

    fun hasNoStartedTimers() = !hasStartedTimers()

    fun hasNoWorkingTimers() = !hasWorkingTimers()
}