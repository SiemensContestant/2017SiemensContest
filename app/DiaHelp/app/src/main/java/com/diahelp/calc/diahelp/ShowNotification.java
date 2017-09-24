package com.diahelp.calc.diahelp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.io.Serializable;
import java.util.Map;

public class ShowNotification extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onCreate();
        Intent i = new Intent(this, EndBGActivity.class);
        i.putExtra("data", intent.getSerializableExtra("data"));
        Map<String, Double> features = (Map<String, Double>) intent.getSerializableExtra("features");
        i.putExtra("features", (Serializable) features);
        i.putExtra("meal num", intent.getIntExtra("meal num", 0));
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT))
                .setSmallIcon(R.drawable.ic_get_dosage)
                .setContentTitle("Enter After Meal Blood Glucose")
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("Enter After Meal Blood Glucose")
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentText("Neccesary to calculate future insulin dosages!")
                .setWhen(System.currentTimeMillis() + 20000); //in 3 hours
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(GetResultActivity.this);
//        stackBuilder.addParentStack(EndBGActivity.class);
//        stackBuilder.addNextIntent(i);
        Notification notif = mBuilder.build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int)(Math.random() * 10000), notif);

        return Service.START_NOT_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
