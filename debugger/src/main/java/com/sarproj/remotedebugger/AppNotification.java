package com.sarproj.remotedebugger;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

class AppNotification {
    private static final String GROUP_KEY = "remote_debugger_group";
    private static final String CHANNEL_ID = "important_channel";
    private static final String CHANNEL_NAME = "Information notifications";
    private static final int NOTIFICATION_ID = 7265;
    private static AppNotification instance;
    private final Context context;
    private NotificationManager notificationManager;
    private NotificationChannel channel;

    private AppNotification(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_NAME);
            channel.enableVibration(false);
        }
    }

    static void init(Context context) {
        if (instance == null) {
            instance = new AppNotification(context);
        }
    }

    static void destroy() {
        instance = null;
    }

    public static void notify(String title, String description) {
        if (instance == null) return;

        instance.notification(title, description);
    }

    private void notification(@Nullable String title, @Nullable String description) {
        if (title == null && description == null) {
            return;
        }

        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(channel);
            }

            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            //noinspection deprecation
            builder = new NotificationCompat.Builder(context);
        }

        builder.setSmallIcon(android.R.drawable.star_on)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setGroup(GROUP_KEY);

            NotificationCompat.Builder groupBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.star_on)
                    .setGroup(GROUP_KEY)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0L})
                    .setGroupSummary(true);

            notificationManager.notify(NOTIFICATION_ID, builder.build());
            notificationManager.notify(GROUP_KEY.hashCode(), groupBuilder.build());
        } else {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
