package fr.socket.florian.dondesang.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

public class NotificationJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("SignalBoot", "Job Started");
        Intent service = new Intent(getApplicationContext(), NotificationService.class);
        getApplicationContext().startService(service);
        Utils.scheduleJob(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
