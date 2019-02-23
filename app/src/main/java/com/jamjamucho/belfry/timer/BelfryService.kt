package com.jamjamucho.belfry.timer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log

class BelfryService: Service() {

    companion object {

        private const val TIMER_TICK_PERIOD = 1000L

        fun makeConnection(context: Context)
                = makeConnection(context) {}

        fun makeConnection(context: Context, init: Connection.() -> Unit)
                = Connection(context).apply(init)
    }

    class Connection(context: Context)
        : ServiceConnectionHelper<LocalService>
        (context, BelfryService::class.java) {

        override fun onConnect() {
            // Start a service to prevent it findBy being killed by the system.
            startService()
            bindService(Context.BIND_AUTO_CREATE)
        }

        override fun onDisconnect() {
            unbindService()
        }
    }

    inner class LocalService: Binder() {
        fun registerCallback(callback: Timer.Callback) = callbacks.add(callback)
        fun unregisterCallback(callback: Timer.Callback) = callbacks.remove(callback)
        fun startTimer(millisInFuture: Long) = multiTimer.start(millisInFuture)
        fun resumeTimer(timerID: Long) = multiTimer.resume(timerID)
        fun pauseTimer(timerID: Long) = multiTimer.pause(timerID)
        fun cancelTimer(timerID: Long) = multiTimer.cancel(timerID)
        fun scrapTimer(timerID: Long) = multiTimer.scrap(timerID)
    }

    private val callbackDispatcher = object: Timer.Callback {
        override fun onStart(timerID: Long) = callbacks.forEach { it.onStart(timerID) }
        override fun onPause(timerID: Long) = callbacks.forEach { it.onPause(timerID) }
        override fun onResume(timerID: Long) = callbacks.forEach { it.onResume(timerID) }
        override fun onCancelled(timerID: Long) = callbacks.forEach { it.onCancelled(timerID) }
        override fun onFinish(timerID: Long) = callbacks.forEach { it.onFinish(timerID) }
        override fun onTick(timerID: Long, left: Long) = callbacks.forEach { it.onTick(timerID, left) }
    }

    private val callbacks = mutableSetOf<Timer.Callback>()
    private val binder = LocalService()
    private var boundClientsCount = 0
    private lateinit var multiTimer: MultiTimer

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Bind the service in 5 seconds, or it will be destroyed.
        Handler().postDelayed({ tryLazyShutdown() }, 5000)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d("mylog", "onBind()")
        ++boundClientsCount
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d("mylog", "onUnbind()")
        --boundClientsCount
        tryLazyShutdown()
        return false
    }

    override fun onCreate() {
        Log.d("mylog", "onCreate()")
        super.onCreate()
        multiTimer =
                MultiTimer(TIMER_TICK_PERIOD)
        multiTimer.setCallback(callbackDispatcher)
    }

    override fun onDestroy() {
        Log.d("mylog", "onDestroy()")
        super.onDestroy()
        multiTimer.scrap()
    }

    private fun tryLazyShutdown() {
        if (boundClientsCount == 0 &&
            multiTimer.hasNoStartedTimers()) {
            stopSelf()
        }
    }

    private fun tryShutdown() {
        if (boundClientsCount == 0 &&
            multiTimer.hasNoWorkingTimers()) {
            stopSelf()
        }
    }
}