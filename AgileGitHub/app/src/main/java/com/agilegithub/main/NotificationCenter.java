package com.agilegithub.main;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Uguudei on 5/6/2015.
 */
public class NotificationCenter implements IFragmentView {

    private NotificationManager mNotificationManager;
    private int SIMPLE_NOTFICATION_ID;
    private Activity activity;
    View notificationServiceView;
    NotificationCompat.Builder notifyDetails;
    public NotificationCenter(Activity activity, View notificationServiceView) {
        this.activity = activity;
        this.notificationServiceView = notificationServiceView;
    }

    public View getView(){
        final NotificationManager mNotificationManager;

        mNotificationManager =
                (NotificationManager)activity.getSystemService(activity.NOTIFICATION_SERVICE);


        Button start = (Button)notificationServiceView.findViewById(R.id.notifyButton);
        Button cancel = (Button)notificationServiceView.findViewById(R.id.cancelButton);

        start.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                Context context = activity.getApplicationContext();
                CharSequence contentTitle =
                        "Notification Details...";
                CharSequence contentText =
                        "New Conflicting commit!";
                Intent notifyIntent =
                        new Intent(MainActivity.activity, LoginActivity.class);
                PendingIntent intent =
                        PendingIntent.getActivity(activity, 0, notifyIntent, Intent.FILL_IN_DATA);
                notifyDetails =
                        new NotificationCompat.Builder(MainActivity.activity)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(contentTitle)
                                .setContentText(contentText)
                                .setContentIntent(intent);
                notifyDetails.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

                //LED
                notifyDetails.setLights(Color.YELLOW, 3000, 3000);

                mNotificationManager.notify(SIMPLE_NOTFICATION_ID, notifyDetails.build());
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mNotificationManager.cancel(SIMPLE_NOTFICATION_ID);
            }
        });
        return notificationServiceView;
    }

    @Override
    public void updateSyncData() {

    }

}
