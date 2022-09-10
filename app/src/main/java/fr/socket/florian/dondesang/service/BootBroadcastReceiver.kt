package fr.socket.florian.dondesang.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("SignalBoot", "Broadcast received")
        NotificationWorker.schedule()
    }
}
