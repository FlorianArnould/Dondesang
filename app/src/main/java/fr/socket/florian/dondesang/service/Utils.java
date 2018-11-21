package fr.socket.florian.dondesang.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Utils {

    private static final int JOB_ID = 56354732;

    private Utils() {
        throw new UnsupportedOperationException("Cannot create an instance of " + this.getClass().getName());
    }

    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, NotificationJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceComponent);
        builder.setMinimumLatency(60 * 1000); // wait at least
        builder.setOverrideDeadline(10 * 60 * 60 * 1000); // maximum delay
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            jobScheduler.schedule(builder.build());
        }
        Log.d("SignalBoot", "Job scheduled");
    }

    public static void scheduleAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 2 * 60 * 1000, alarmIntent);
        Log.d("SignalAlarm", "alarm scheduled");
    }

    public static boolean isNotificationJobRunning(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler != null) {
            for (JobInfo job : jobScheduler.getAllPendingJobs()) {
                if (job.getId() == JOB_ID) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAlarmSet(Context context) {
        return PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null;
    }
}
