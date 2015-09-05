package com.tonini.diego.dexpense;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar now = GregorianCalendar.getInstance(Locale.getDefault());

        int sec = now.get(Calendar.SECOND);
        Log.i(MainActivity.TAG, "AlarmReceiver call with seconds"+sec);

        Intent i = new Intent(context,RepeatingService.class);
        context.startService(i);

    }


}
