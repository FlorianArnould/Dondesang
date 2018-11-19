package fr.socket.florian.dondesang.loader;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import fr.socket.florian.dondesang.R;
import fr.socket.florian.dondesang.model.User;

public class HistoryManager {
    @VisibleForTesting static final String PREFERENCES_FILE_NAME = "history";
    private static final String IS_THERE_AN_HISTORY = "is_there_an_history";

    @VisibleForTesting static final String BLOOD_DONATIONS = "blood_donations";
    @VisibleForTesting static final String PLATELETS_DONATIONS = "platelets_donations";
    @VisibleForTesting static final String PLASMA_DONATIONS = "plasma_donations";

    private static final String CONGRATS_CHANNEL = "congrats_channel";

    private HistoryManager() {
        throw new UnsupportedOperationException("Cannot create an instance of " + this.getClass().getName());
    }

    public static void saveAndCheck(Context context, @NonNull User user) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        if (preferences.getBoolean(IS_THERE_AN_HISTORY, false)) {
            check(context, preferences, user);
        }
        save(preferences, user);
    }

    private static void check(Context context, SharedPreferences preferences, User user) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel(CONGRATS_CHANNEL, "Félicitations", NotificationManager.IMPORTANCE_LOW);
                channel.setDescription("Félicitations d'après don");
                notificationManager.createNotificationChannel(channel);
            }
        }
        if (preferences.getInt(BLOOD_DONATIONS, 0) < user.getNbBloodDonations()) {
            sendNotification(context, context.getString(R.string.blood));
        }
        if (preferences.getInt(PLATELETS_DONATIONS, 0) < user.getNbPlateletsDonations()) {
            sendNotification(context, context.getString(R.string.platelets));
        }
        if (preferences.getInt(PLASMA_DONATIONS, 0) < user.getNbPlasmaDonations()) {
            sendNotification(context, context.getString(R.string.plasma));
        }
    }

    private static void sendNotification(Context context, String donationType) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CONGRATS_CHANNEL)
                        .setSmallIcon(R.drawable.ic_droplet)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_droplet))
                        .setContentTitle(context.getString(R.string.congrats_title))
                        .setContentText(context.getString(R.string.congrats_message) + " " + donationType)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMgr.notify(1, builder.build());
    }

    private static void save(SharedPreferences preferences, User user) {
        preferences.edit().putBoolean(IS_THERE_AN_HISTORY, true)
                .putInt(BLOOD_DONATIONS, user.getNbBloodDonations())
                .putInt(PLATELETS_DONATIONS, user.getNbPlateletsDonations())
                .putInt(PLASMA_DONATIONS, user.getNbPlasmaDonations())
                .apply();
    }
}
