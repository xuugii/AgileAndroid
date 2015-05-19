package com.agilegithub.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by ugsu7903 on 2015-04-22.
 */
public class GitHubService extends Service {
    private Handler serviceHandler;
    private volatile int counter;
    private volatile int initSizeCommit = -1;
    private volatile int changeSizeCommit;
    private boolean firstTime = true;
    private Task myTask = new Task();
    private String userName;
    private String password;
    private String repoName;
    private boolean bound = false;
    public static String TAG = "GitHubService";
    public static boolean serviceStarted = false;
    //    Help function with Github
    private static ScheduledExecutorService worker;
    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "onBind()");
        bound = true;
        counter = 0;
        mNotificationManager = (NotificationManager)getApplicationContext().getSystemService(this.NOTIFICATION_SERVICE);
        if (userName == null || userName == "") {

            Bundle b = arg0.getExtras();

            Log.d(TAG, "onBind" + b.getString("userName"));

            Log.d(TAG, arg0.getStringExtra("userName"));
            userName = b.getString("userName");
            password = b.getString("password");
            repoName = b.getString("repoName");
        }
        return remoteServiceStub;
    }

    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind()");
        bound = false;
        super.onUnbind(intent);
        return true;
    }



    private IRemoteService.Stub remoteServiceStub =
            new IRemoteService.Stub() {
                public int getCounter() throws RemoteException {
                    return counter;
                }

                public int getCommitSize() throws RemoteException {
                    return changeSizeCommit;
                }};

    NotificationManager mNotificationManager;
    NotificationCompat.Builder notifyDetails;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (MainActivity.activity == null || serviceStarted)
            return START_NOT_STICKY;
//        TODO: fix the icon for the notification
        worker = Executors.newSingleThreadScheduledExecutor();
        Context context = getApplicationContext();
        CharSequence contentTitle = "Notification Details...";
        CharSequence contentText = "New Conflicting commit!";
        Intent notifyIntent = new Intent(MainActivity.activity,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, Intent.FILL_IN_DATA);
        showOnceNotification = true;
        notifyDetails =
                new NotificationCompat.Builder(MainActivity.activity)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setContentIntent(pendingIntent);
        notifyDetails.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        notifyDetails.setLights(Color.YELLOW, 3000, 3000);
        serviceStarted = true;

        serviceHandler = new Handler();
        serviceHandler.postDelayed(myTask, 1000L);
        Log.d(TAG, "onStart()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        initSizeCommit = GitHubViewListAdapter.gitHub.getListCommits().size();
        bound = false;
        Log.d(TAG, "onCreate()");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceHandler.removeCallbacksAndMessages(myTask);
        bound = false;
        worker.shutdownNow();
        serviceHandler = null;
        serviceStarted = false;
        Log.d(TAG, "onDestroy()");
    }

    public volatile boolean showOnceNotification;
    public synchronized void getCommit(){
        Runnable task = new Runnable() {
            public void run() {
//                TODO: make GitHubServer that stores all the list and information
                if (GitHubParser.gitHub == null) {
                    GitHubParser.gitHubParser();
                } else {
                    changeSizeCommit = GitHubParser.gitHubParser().getListCommits().size();
                    if (firstTime) {
                        initSizeCommit = changeSizeCommit;
                        firstTime = false;
                    }
                    if (initSizeCommit != -1){
                        if (changeSizeCommit > initSizeCommit){
                            mNotificationManager.notify(0, notifyDetails.build());
                         }
                    }
                    if (showOnceNotification && counter>=10){
                        mNotificationManager.notify(0, notifyDetails.build());
                        showOnceNotification = false;
                    }

                }
            }
        };
        try {
            worker.schedule(task, 0, TimeUnit.SECONDS);
        } catch (Exception e){

        }
    }

    class Task implements Runnable {
        public void run() {
            if (serviceHandler != null) {
                if (bound){
                    ++counter;
                    getCommit();
                    Log.i(TAG,
                            "Incrementing counter in the run method");
                }
                serviceHandler.postDelayed(this, 1000L);
            }
        }
    }
}
