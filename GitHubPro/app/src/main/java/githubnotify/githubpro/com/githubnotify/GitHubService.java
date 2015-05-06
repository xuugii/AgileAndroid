package githubnotify.githubpro.com.githubnotify;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by ugsu7903 on 2015-04-22.
 */
public class GitHubService extends Service{
    private Handler serviceHandler;
    private int counter;
    private int initSizeCommit;
    private int changeSizeCommit;
    private Task myTask = new Task();


    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(getClass().getSimpleName(), "onBind()");
        return remoteServiceStub;
    }

    private IRemoteService.Stub remoteServiceStub =
            new IRemoteService.Stub() {
                public int getCounter() throws RemoteException {
                    return counter;
                }

                public int getCommitSize() throws RemoteException {
                    return changeSizeCommit;
                }};

    @Override
    public void onCreate() {
        super.onCreate();
//        initSizeCommit = GitHubViewListAdapter.gitHub.getListCommits().size();
        Log.d(getClass().getSimpleName(),"onCreate()");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceHandler.removeCallbacks(myTask);
        serviceHandler = null;
        Log.d(getClass().getSimpleName(),"onDestroy()");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        serviceHandler = new Handler();
        serviceHandler.postDelayed(myTask, 1000L);
        Log.d(getClass().getSimpleName(), "onStart()");
    }


    private final ScheduledExecutorService worker = Executors
            .newSingleThreadScheduledExecutor();

    public void getCommit(){
        Runnable task = new Runnable() {
            public void run() {
//                TODO: make GitHubServer that stores all the list and information
                changeSizeCommit=GitHubViewListAdapter.gitHub.getListCommits().size();
            }
        };
        worker.schedule(task, 0, TimeUnit.SECONDS);
    }

    class Task implements Runnable {
        public void run() {
            ++counter;
            getCommit();
            serviceHandler.postDelayed(this,10000L);
            Log.i(getClass().getSimpleName(),
                    "Incrementing counter in the run method");
        }
    }
}
