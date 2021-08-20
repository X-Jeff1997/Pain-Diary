package com.example.fit5046_a3.alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String ChannelID = "myChannel";
    private static final CharSequence ChannelName = "Alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder builder = notificationHelper.getChannelNotification()
                .setContentTitle("Pain Diary")
                .setContentText("Daily Reminder!");

        notificationHelper.getManager().notify(1, builder.build());
    }
}

