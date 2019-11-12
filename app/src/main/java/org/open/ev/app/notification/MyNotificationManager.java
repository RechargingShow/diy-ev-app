package org.open.ev.app.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class MyNotificationManager {

    private static final String CHANNEL_NAME = "ev-ap-channel";
    private static final String CHANNEL_DESCRIPTION = "Notifications for hyundai kona ev app";
    public static final String CHANNEL_ID = "ev-app-notifications";
    public static final int NOTIFICATION_ID = 69;

    public static void createNotificationChannel(Context context){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
