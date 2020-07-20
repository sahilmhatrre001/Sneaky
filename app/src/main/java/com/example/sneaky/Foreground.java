package com.example.sneaky;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Foreground extends Application {
    public static final String CHANNEL_ID = "foreGroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sneaky",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }
}
