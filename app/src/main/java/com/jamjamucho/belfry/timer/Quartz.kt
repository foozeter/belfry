package com.jamjamucho.belfry.timer

import android.os.Handler
import android.os.SystemClock
import java.util.concurrent.atomic.AtomicBoolean

class Quartz(val period: Long = DEFAULT_PERIOD) {
    var isAlreadyActivated = false; private set
    private val listeners = mutableSetOf<OnOscillateListener>()
    private val receiver = Handler()
    private val quitFlag = AtomicBoolean(false)

    private fun onOscillate() {
        listeners.forEach { it.onOscillate() }
    }

    @Synchronized
    fun activate() {
        if (!isAlreadyActivated) {
            isAlreadyActivated = true
            Thread {
                var delay: Long
                while (!quitFlag.get()) {
                    delay = SystemClock.elapsedRealtime()
                    receiver.post { onOscillate() }
                    delay += period - SystemClock.elapsedRealtime()
                    while (delay < 0) delay += period
                    Thread.sleep(delay)
                }
            }.start()
        }
    }

    fun quit() {
        if (isAlreadyActivated) quitFlag.set(true)
    }

    fun addOnOscillateListener(listener: OnOscillateListener) {
        listeners.add(listener)
    }

    fun removeOnOscillateListener(listener: OnOscillateListener) {
        listeners.remove(listener)
    }

    interface OnOscillateListener {
        fun onOscillate()
    }

    companion object {
        private const val DEFAULT_PERIOD = 1000L
    }
}