package githubnotify.githubpro.com.githubnotify;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, OnClickListener {
    public static boolean loggedIn = false;
    public static Activity activity;
    static GitHubParser gitHub;
    static Button start;
    static Button stop;
    static Button bind;
    static Button invoke;
    static Button release;
    public static int MENU_SIZE = 4;
    public static List<PlaceholderFragment> listFragment = new ArrayList<>();


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public MainActivity(){
        for (int i = 0; i < MENU_SIZE; i++) {
            listFragment.add(PlaceholderFragment.newInstance(i + 1));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        activity = this;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, listFragment.get(position))
                .commit();

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
    }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public View savedView = null;

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            int menu = getArguments().getInt(ARG_SECTION_NUMBER);
            switch (menu) {
                case 1:
                    if (savedView == null) {
                        savedView = (new GitHubView(activity)).getListView();
                        return savedView;
                    }
                    return savedView;
                case 2:
                    break;
                case 3:
                    return (new PlanningPokerView(activity)).getView();
                case 4:
                    View rootViewService = inflater.inflate(R.layout.service_view, container, false);
                    start = (Button) rootViewService.findViewById(R.id.serviceStart);
                    stop = (Button) rootViewService.findViewById(R.id.serviceStop);
                    bind = (Button) rootViewService.findViewById(R.id.serviceBind);
                    invoke = (Button) rootViewService.findViewById(R.id.serviceInvoke);
                    release = (Button) rootViewService.findViewById(R.id.serviceRelease);

                    start.setOnClickListener((OnClickListener) activity);
                    stop.setOnClickListener((OnClickListener) activity);
                    bind.setOnClickListener((OnClickListener) activity);
                    invoke.setOnClickListener((OnClickListener) activity);
                    release.setOnClickListener((OnClickListener) activity);
                    return rootViewService;

            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    /**
     * Network on another thread
     */



    /**
     * Remote Service Helper
     */
    private IRemoteService remoteService;
    private boolean started = false;
    private RemoteServiceConnection conn = null;


    private void startService(){
        if (started) {
            Toast.makeText(MainActivity.this,
                    "Service already started", Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent();
            i.setClassName("githubnotify.githubpro.com.githubnotify",
                    "githubnotify.githubpro.com.githubnotify.GitHubService");
            startService(i);
            started = true;
            updateServiceStatus();
            Log.d( getClass().getSimpleName(), "startService()" );
        }
    }

    private void stopService() {
        if (!started) {
            Toast.makeText(MainActivity.this,
                    "Service not yet started", Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent();
            i.setClassName("githubnotify.githubpro.com.githubnotify",
                    "githubnotify.githubpro.com.githubnotify.GitHubService");
            stopService(i);
            started = false;
            updateServiceStatus();
            Log.d( getClass().getSimpleName(), "stopService()" );
        }
    }

    private void bindService() {
        if(conn == null) {
            conn = new RemoteServiceConnection();
            Intent i = new Intent();
            i.setClassName("githubnotify.githubpro.com.githubnotify",
                    "githubnotify.githubpro.com.githubnotify.GitHubService");
            bindService(i, conn, Context.BIND_AUTO_CREATE);
            updateServiceStatus();
            Log.d( getClass().getSimpleName(), "bindService()" );
        } else {
            Toast.makeText(MainActivity.this,
                    "Cannot bind - service already bound", Toast.LENGTH_SHORT).show();
        }
    }

    private void releaseService() {
        if(conn != null) {
            unbindService(conn);
            conn = null;
            updateServiceStatus();
            Log.d( getClass().getSimpleName(), "releaseService()" );
        } else {
            Toast.makeText(MainActivity.this,
                    "Cannot unbind - service not bound", Toast.LENGTH_SHORT).show();
        }
    }

    private void invokeService() {
        if(conn == null) {
            Toast.makeText(MainActivity.this,
                    "Cannot invoke - service not bound", Toast.LENGTH_SHORT).show();
        } else {
            try {
                int counter = remoteService.getCounter();
                TextView t = (TextView)findViewById(R.id.serviceCounter);
                t.setText( "Counter value: "+Integer.toString( counter ) );
                Log.d( getClass().getSimpleName(), "invokeService()" );
            } catch (RemoteException re) {
                Log.e( getClass().getSimpleName(), "RemoteException" );
            }
        }
    }

    class RemoteServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className,
                                       IBinder boundService ) {
            remoteService = IRemoteService.Stub.asInterface(boundService);
            Log.d( getClass().getSimpleName(), "onServiceConnected()" );
        }

        public void onServiceDisconnected(ComponentName className) {
            remoteService = null;
            updateServiceStatus();
            Log.d( getClass().getSimpleName(), "onServiceDisconnected" );
        }
    }

    private void updateServiceStatus() {
        String bindStatus = conn == null ? "unbound" : "bound";
        String startStatus = started ? "started" : "not started";
        String statusText = "Service status: "+
                bindStatus+ ","+ startStatus;
        TextView t = (TextView)findViewById( R.id.serviceStatus);
        t.setText( statusText );
    }

    protected void onDestroy() {
        super.onDestroy();
        releaseService();
        Log.d( getClass().getSimpleName(), "onDestroy()" );
    }

    @Override
    public void onClick(View view) {
        if (start.isPressed()){
            startService();
        } else if (stop.isPressed()){
            stopService();
        } else if (bind.isPressed()){
            bindService();
        } else if (invoke.isPressed()){
            invokeService();
        } else if (release.isPressed()){
            releaseService();
        }
    }

}
