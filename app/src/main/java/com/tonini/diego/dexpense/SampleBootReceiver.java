package com.tonini.diego.dexpense;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SampleBootReceiver extends BroadcastReceiver {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public SampleBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        Log.i(MainActivity.TAG,"SampleBootReceiver stared");

        Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
        calendar.add(Calendar.SECOND,30);

        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                1000 * 60 * 20, alarmIntent);
    }
}
