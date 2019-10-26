package com.sarproj.remotedebugger;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

    public static void notify(@Nullable String title, @Nullable String description) {
        if (instance == null) return;

        instance.notification(title, description, false);
    }

    public static void notifyError(@Nullable String title, @Nullable String description) {
        if (instance == null) return;

        instance.notification(title, description, true);
    }

    private void notification(@Nullable String title, @Nullable String description, boolean isError) {
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

        if (isError) {
            Intent repeatConnectionIntent = new Intent(context, NotificationReceiver.class);
            repeatConnectionIntent.setAction(NotificationReceiver.ACTION_REPEAT_CONNECTION);
            PendingIntent repeatConnectionPendingIntent = PendingIntent.getBroadcast(context, 0, repeatConnectionIntent, 0);

            Intent disableOtherIntent = new Intent(context, NotificationReceiver.class);
            disableOtherIntent.setAction(NotificationReceiver.ACTION_DISABLE_OTHER);
            PendingIntent disableOtherPendingIntent = PendingIntent.getBroadcast(context, 0, disableOtherIntent, 0);

            builder.addAction(0, "Repeat", repeatConnectionPendingIntent);
            builder.addAction(0, "Disable others", disableOtherPendingIntent);
        }

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
