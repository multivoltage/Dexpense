package com.tonini.diego.dexpense;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tonini.diego.dexpense.database.DBHelper;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class RepeatingService extends IntentService {

    private DBHelper dbHelper;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RepeatingService(String name) {
        super(name);
    }
    public RepeatingService(){
        this("com.diego.tonin.threadrepeatingservice");
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
    }


    @Override
    public void onDestroy(){

        Log.i(MainActivity.TAG,"RepeatingService stopped");
        dbHelper.close();
        super.onDestroy();
    }

    public void notice(Context context, String title, String contentTitle) {
        // Set Notification Title
        String strtitle = "title notice";

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, MainActivity.class);
        // Send data to NotificationView Class
        intent.putExtra("title", strtitle);

        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                // Set Icon
                .setSmallIcon(R.drawable.tear_of_calendar_128)
                        // Set Ticker Message
                .setTicker(title)
                        // Set Title
                .setContentTitle(contentTitle)
                        // Set Text
                .setContentText(title)
                        // Add an Action Button below Notification
                .addAction(R.mipmap.ic_launcher, "Action Button", pIntent)
                        // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                        // Dismiss Notification
                .setAutoCancel(true);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }
}
