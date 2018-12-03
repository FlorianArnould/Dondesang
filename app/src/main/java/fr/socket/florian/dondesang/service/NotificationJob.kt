package fr.socket.florian.dondesang.service

import android.content.Context
import android.util.Log
import com.evernote.android.job.DailyJob
import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import fr.socket.florian.dondesang.loader.HistoryManager
import fr.socket.florian.dondesang.loader.Loader
import java.util.concurrent.TimeUnit

class NotificationJob : Job() {
    override fun onRunJob(params: Params): Result {
        val loader = Loader().initialize(context)
        return if (loader == null) Result.FAILURE
        else {
            val user = loader.loadUser()
            if (user == null) Result.FAILURE
            else {
                HistoryManager.saveAndCheck(context, user)
                Log.d("Job", "User loaded")
                Result.SUCCESS
            }
        }
    }

    companion object {
        private const val TAG = "notification_job_tag"
        private const val MIN_HOUR: Long = 6
        private const val MAX_HOUR: Long = 9

        fun schedule(context: Context): Int {
            create(context)
            val builder = JobRequest.Builder(NotificationJob.TAG)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
            return DailyJob.schedule(builder, TimeUnit.HOURS.toMillis(MIN_HOUR), TimeUnit.HOURS.toMillis(MAX_HOUR))
        }

        private fun create(context: Context) {
            JobManager.create(context).addJobCreator {
                when (it) {
                    TAG -> NotificationJob()
                    else -> null
                }
            }
        }
    }
}