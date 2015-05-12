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
    private int counter;
    private volatile int initSizeCommit = -1;
    private volatile int changeSizeCommit;
    private boolean firstTime = true;
    private Task myTask = new Task();
    private String userName;
    private String password;
    private String repoName;
    private boolean bound = false;
    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(getClass().getSimpleName(), "onBind()");
        bound = true;
        if (userName == null || userName == "") {

            Bundle b = arg0.getExtras();

            Log.d(getClass().getSimpleName(), "onBind" + b.getString("userName"));

            Log.d(getClass().getSimpleName(), arg0.getStringExtra("userName"));
            userName = b.getString("userName");
            password = b.getString("password");
            repoName = b.getString("repoName");
        }
        return remoteServiceStub;
    }
    @Override
    public boolean onUnbind(Intent intent){
        Log.d(getClass().getSimpleName(), "onUnbind");
        bound = false;
        return onUnbind(intent);
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
        mNotificationManager =
                (NotificationManager)getApplicationContext().getSystemService(this.NOTIFICATION_SERVICE);
//        TODO: fix the icon for the notification

        Context context = getApplicationContext();
        CharSequence contentTitle = "Notification Details...";
        CharSequence contentText = "New Conflicting commit!";
        Intent notifyIntent = new Intent(MainActivity.activity,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, Intent.FILL_IN_DATA);

        notifyDetails =
                new NotificationCompat.Builder(MainActivity.activity)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setContentIntent(pendingIntent);
        notifyDetails.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        notifyDetails.setLights(Color.YELLOW, 3000, 3000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        initSizeCommit = GitHubViewListAdapter.gitHub.getListCommits().size();
        bound = false;
        Log.d(getClass().getSimpleName(), "onCreate()");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceHandler.removeCallbacks(myTask);
        serviceHandler = null;
        Log.d(getClass().getSimpleName(), "onDestroy()");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (userName == null || userName == "") {
            userName = LoginActivity.userName;
            password = LoginActivity.password;
            gitHub = GitHubViewListAdapter.gitHub;
            repoName = LoginActivity.repoName;
            Log.d(getClass().getSimpleName(), "onStart" + intent.getStringExtra("userName"));
        }
        serviceHandler = new Handler();
        serviceHandler.postDelayed(myTask, 1000L);
        Log.d(getClass().getSimpleName(), "onStart()");
    }


    public volatile boolean showOnceNotification = true;
    public synchronized void getCommit(){
        Runnable task = new Runnable() {
            public void run() {
//                TODO: make GitHubServer that stores all the list and information
                if (gitHub == null) {
                    initGitHub();
                } else {
                    changeSizeCommit = GitHubViewListAdapter.gitHub.getListCommits().size();
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
        worker.schedule(task, 0, TimeUnit.SECONDS);
    }

    class Task implements Runnable {
        public void run() {
            ++counter;
            if (bound)
                getCommit();
            serviceHandler.postDelayed(this,1000L);
            Log.i(getClass().getSimpleName(),
                    "Incrementing counter in the run method");
        }
    }

//    Help function with Github
private static final ScheduledExecutorService worker = Executors
        .newSingleThreadScheduledExecutor();

    GitHubParser gitHub = null;

    void initGitHub(){
        Runnable task = new Runnable() {
            public void run() {
                    gitHub = new GitHubParser(userName,password, LoginActivity.repoName);
            }
        };
        worker.schedule(task, 0, TimeUnit.SECONDS);
    }
}
