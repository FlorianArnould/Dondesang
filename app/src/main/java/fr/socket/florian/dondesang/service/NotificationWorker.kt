package fr.socket.florian.dondesang.service

import android.content.Context
import android.util.Log
import androidx.work.*
import fr.socket.florian.dondesang.loader.HistoryManager
import fr.socket.florian.dondesang.loader.Loader
import java.util.concurrent.TimeUnit

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val loader = Loader().initialize(applicationContext)
        return if (loader == null) Result.failure()
        else {
            val user = loader.loadUser()
            if (user == null) Result.failure()
            else {
                HistoryManager.saveAndCheck(applicationContext, user)
                Log.d("Job", "User loaded")
                Result.success()
            }
        }
    }

    companion object {
        const val TAG = "notification_worker_tag"


        fun schedule() {
            val request = PeriodicWorkRequestBuilder<NotificationWorker>(12, TimeUnit.HOURS, 3, TimeUnit.HOURS)
                .addTag(TAG)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            WorkManager.getInstance().enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP, request.build())
        }

    }
}