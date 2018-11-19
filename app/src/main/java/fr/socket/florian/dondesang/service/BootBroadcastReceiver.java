package fr.socket.florian.dondesang.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("SignalBoot", "Broadcast received");
        //Utils.scheduleJob(context);
        Utils.scheduleAlarm(context);
    }
}
