package githubnotify.githubpro.com.githubnotify;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


/**
 * Created by ugsu7903 on 2015-04-22.
 */
public class GitHubService extends Service{
    private Handler serviceHandler;
    private int counter;
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
                }};

    @Override
    public void onCreate() {
        super.onCreate();
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

    class Task implements Runnable {
        public void run() {
            ++counter;
            serviceHandler.postDelayed(this,1000L);
            Log.i(getClass().getSimpleName(),
                    "Incrementing counter in the run method");
        }
    }
}
