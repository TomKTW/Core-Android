package dev.kobalt.core.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dev.kobalt.core.application.Application

class Broadcaster(val application: Application, val filter: IntentFilter) :
    BroadcastReceiver() {

    private var registered = false

    private val listeners = mutableListOf<(Data) -> Unit>()

    override fun onReceive(context: Context?, intent: Intent?) {
        listeners.forEach {
            it.invoke(Data(intent?.action, intent?.extras?.keySet()?.associate { key ->
                key to intent.extras?.get(key)
            }.orEmpty()))
        }
    }

    fun addListener(listener: (Data) -> Unit) {
        listeners.add(listener)
        if (!registered && listeners.isNotEmpty()) {
            registered = true
            application.native.registerReceiver(this, filter)
        }
    }

    fun removeListener(listener: (Data) -> Unit) {
        listeners.add(listener)
        if (registered && listeners.isEmpty()) {
            registered = false
            application.native.unregisterReceiver(this)
        }
    }

    data class Data(
        val action: String?,
        val values: Map<String, Any?>
    )
}