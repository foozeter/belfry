package com.jamjamucho.belfry.timer

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

abstract class ServiceConnectionHelper<T>(
    private val context: Context,
    private val service: Class<out Service>) {
    private var onConnectedListener = {_: T -> }
    private var onDisconnectedListener = {}
    private var onBindingDiedListener = {}
    private var onNullBindingListener = {}
    var isConnected = false; private set
    var binder: T? = null; private set

    private val connectionListener = object: ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            isConnected = false
            binder = null
            onDisconnectedListener()
        }

        @Suppress("UNCHECKED_CAST")
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            isConnected = true
            binder = service as T
            onConnectedListener(service)
        }

        override fun onBindingDied(name: ComponentName?) {
            isConnected = false
            binder = null
            onBindingDiedListener()
        }

        override fun onNullBinding(name: ComponentName?) {
            isConnected = true
            binder = null
            onNullBindingListener()
        }
    }

    fun connect(onConnected: (binder: T) -> Unit) {
        onConnectedListener = onConnected
        connect()
    }

    fun connect() {
        if (isConnected) {
            val binder = binder
            if (binder != null) onConnectedListener(binder)
            else onNullBindingListener()
        } else onConnect()
    }

    fun disconnect() {
        if (!isConnected) onDisconnect()
    }

    fun onConnected(listener: (binder: T) -> Unit) {
        onConnectedListener = listener
    }

    fun onDisconnected(listener: () -> Unit) {
        onDisconnectedListener = listener
    }

    fun onBindingDied(listener: () -> Unit) {
        onBindingDiedListener = listener
    }

    fun onNullBinding(listener: () -> Unit) {
        onNullBindingListener = listener
    }

    protected abstract fun onConnect()

    protected abstract fun onDisconnect()

    protected fun startService() {
        context.startService(Intent(context, service))
    }

    protected fun bindService(flag: Int) {
        context.bindService(Intent(context, service), connectionListener, flag)
    }

    protected fun stopService() {
        context.stopService(Intent(context, service))
    }

    protected fun unbindService() {
        context.unbindService(connectionListener)
    }
}