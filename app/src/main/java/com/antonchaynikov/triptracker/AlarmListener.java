package com.antonchaynikov.triptracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class AlarmListener extends BroadcastReceiver {

    public static void initAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(AppCompatActivity.ALARM_SERVICE);
        if (alarmManager != null) {
            PendingIntent intent = PendingIntent.getBroadcast(context, 0, AlarmListener.makeIntent(context), 0);
            AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.ELAPSED_REALTIME_WAKEUP, TimeUnit.SECONDS.toMillis(15), intent);
        }
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, AlarmListener.class);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null) {
            Toast.makeText(context, "Received!", Toast.LENGTH_LONG).show();
            //initAlarm(context);
        }
    }
}
