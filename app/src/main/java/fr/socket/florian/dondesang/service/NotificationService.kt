package fr.socket.florian.dondesang.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import fr.socket.florian.dondesang.loader.HistoryManager
import fr.socket.florian.dondesang.loader.Loader

class NotificationService : Service() {
    private var _binder: Binder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("SignalBoot", "NotificationService created")
        _binder = ServiceBinder()
    }

    override fun onBind(intent: Intent): IBinder? {
        return _binder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Loader().initialize(applicationContext) { loader ->
            loader?.asyncLoadUser { user ->
                if (user != null) {
                    HistoryManager.saveAndCheck(applicationContext, user)
                    Log.d("SignalAlarm", "User loaded")
                }
            }
        }
        Log.d("SignalAlarm", "NotificationService executed")
        return Service.START_NOT_STICKY
    }

    internal inner class ServiceBinder : Binder() {
        val serviceInstance: NotificationService
            get() = this@NotificationService
    }
}
