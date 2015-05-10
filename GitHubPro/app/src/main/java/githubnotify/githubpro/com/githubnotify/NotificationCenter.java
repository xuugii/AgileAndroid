package githubnotify.githubpro.com.githubnotify;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    public NotificationCenter(Activity activity, View notificationServiceView) {
        this.activity = activity;
        this.notificationServiceView = notificationServiceView;
    }

    public NotificationCenter(Activity activity) {
        this.activity = activity;
    }

    public View getView(){
        final NotificationManager mNotificationManager;

        mNotificationManager =
                (NotificationManager)activity.getSystemService(activity.NOTIFICATION_SERVICE);
        final Notification notifyDetails =
                new Notification(R.drawable.android,
                        "You've got a new notification!",System.currentTimeMillis());

        Button start = (Button)notificationServiceView.findViewById(R.id.notifyButton);
        Button cancel = (Button)notificationServiceView.findViewById(R.id.cancelButton);

        start.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                showNotification(activity, notifyDetails);
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mNotificationManager.cancel(SIMPLE_NOTFICATION_ID);
            }
        });
        return notificationServiceView;
    }

    public void requestNotification(){
        final NotificationManager mNotificationManager;

        mNotificationManager =
                (NotificationManager)activity.getSystemService(activity.NOTIFICATION_SERVICE);
        final Notification notifyDetails =
                new Notification(R.drawable.android,
                        "You've got a new notification!",System.currentTimeMillis());
        showNotification(activity, notifyDetails);
    }

    public void showNotification(Activity activity, Notification notifyDetails){
        Context context = activity.getApplicationContext();
        CharSequence contentTitle = "Notification Details...";
        CharSequence contentText = "New Conflicting commit!";
        Intent notifyIntent = new Intent(MainActivity.activity,LoginActivity.class);
        PendingIntent intent = PendingIntent.getActivity(activity, 0, notifyIntent, Intent.FILL_IN_DATA);
        notifyDetails.setLatestEventInfo(context, contentTitle, contentText, intent);
        mNotificationManager.notify(SIMPLE_NOTFICATION_ID, notifyDetails);
    }

}
