package com.agilegithub.main;

import com.agilegithub.main.adapter.NavDrawerListAdapter;
import com.agilegithub.main.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	public static boolean loggedIn = false;
	public static Activity activity;
	static GitHubParser gitHub;
	static Button start;
	static Button stop;
	static Button bind;
	static Button invoke;
	static Button release;
	public static int MENU_SIZE = 7;
	public static List<PlaceholderFragment> listFragment = new ArrayList<PlaceholderFragment>();
	public enum State{loggedIn, logout} ;
	static State state = State.logout;
	static ArrayList<Files> selectedFilesList;

	public MainActivity(){
		for (int i = 0; i < MENU_SIZE; i++) {
			listFragment.add(PlaceholderFragment.newInstance(i + 1));
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.agilegithub.main.R.layout.activity_main);
		if (!LoginActivity.loggedIn){
			state = State.logout;
		}
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(com.agilegithub.main.R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(com.agilegithub.main.R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(com.agilegithub.main.R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(com.agilegithub.main.R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1), true, "50+"));
		

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
//		TODO: fix the icon for the action bar icon ... where it extends
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				com.agilegithub.main.R.string.app_name, // nav drawer open - description for accessibility
				com.agilegithub.main.R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		activity = this;
		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}

		selectedFilesList = new ArrayList<Files>();
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(com.agilegithub.main.R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case com.agilegithub.main.R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(com.agilegithub.main.R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	public void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		fragment = (Fragment)listFragment.get(position);

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(com.agilegithub.main.R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}



	/**
	 * Remote Service Helper
	 */
	private IRemoteService remoteService;
	private boolean started = false;
	private RemoteServiceConnection conn = null;
	private Messenger myService = null;

	private void startService(){
		if (started) {
			Toast.makeText(MainActivity.this,
					"Service already started", Toast.LENGTH_SHORT).show();
		} else {
			Intent i = new Intent();
			i.setClassName("com.agilegithub.main",
					"com.agilegithub.main.GitHubService");
			startService(i);
			started = true;
			updateServiceStatus();
			Log.d(getClass().getSimpleName(), "startService()");
		}
	}

	private void stopService() {
		if (!started) {
			Toast.makeText(MainActivity.this,
					"Service not yet started", Toast.LENGTH_SHORT).show();
		} else {
			Intent i = new Intent();
			i.setClassName("com.agilegithub.main",
					"com.agilegithub.main.GitHubService");
			stopService(i);
			started = false;
			updateServiceStatus();
			Log.d(getClass().getSimpleName(), "stopService()");
		}
	}

	private void bindService() {
		if(conn == null) {
			conn = new RemoteServiceConnection();
			Intent i = new Intent();
			i.setClassName("com.agilegithub.main",
					"com.agilegithub.main.GitHubService");
			i.putExtra("password", LoginActivity.password);
			i.putExtra("userName", LoginActivity.userName);
			i.putExtra("repoName", LoginActivity.repoName);


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
				int changeCommitSize = remoteService.getCommitSize();
				TextView t = (TextView)findViewById(R.id.serviceCounter);
				t.setText( "Counter value: "+Integer.toString( counter ) + "CommitSize: " + Integer.toString(changeCommitSize));
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


	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseService();
		Log.d( getClass().getSimpleName(), "onDestroy()" );
	}

	@Override
	public void onClick(View view) {

		if (start != null) {
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public View savedView = null;
		public int SIMPLE_NOTFICATION_ID;
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
			if (state == State.loggedIn) {
				switch (menu) {
					case 1:
						final View logoutAbout = inflater.inflate(R.layout.login_logout_about_view, container, false);
						Button logout = (Button) logoutAbout.findViewById(R.id.loginLogout);
						logout.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								state = State.logout;
								LoginActivity.logout();
								((MainActivity) activity).displayView(4);
							}
						});
						return logoutAbout;
					case 2:
						return (new SelectFiles(activity, selectedFilesList)).getListView();
					case 3:
						if (savedView == null) {
							savedView = (new GitHubView(activity)).getView();
							return savedView;
						}
						return savedView;
					case 4:
						return (new PlanningPokerView(activity)).getView();
					case 5:
						final View notificationService = inflater.inflate(R.layout.notification_view, container, false);
						return (new NotificationCenter(activity, notificationService)).getView();
					case 6:
						View rootViewService = inflater.inflate(R.layout.service_view, container, false);
						start = (Button) rootViewService.findViewById(R.id.serviceStart);
						stop = (Button) rootViewService.findViewById(R.id.serviceStop);
						bind = (Button) rootViewService.findViewById(R.id.serviceBind);
						invoke = (Button) rootViewService.findViewById(R.id.serviceInvoke);
						release = (Button) rootViewService.findViewById(R.id.serviceRelease);

						start.setOnClickListener((View.OnClickListener) activity);
						stop.setOnClickListener((View.OnClickListener) activity);
						bind.setOnClickListener((View.OnClickListener) activity);
						invoke.setOnClickListener((View.OnClickListener) activity);
						release.setOnClickListener((View.OnClickListener) activity);
						return rootViewService;
                    case 7:
                        if (savedView == null) {
                            savedView = (new CommitExpandListView(activity)).getView();
                            return savedView;
                        }

				}
			} else {
				switch (menu) {
					case 4:
						return (new PlanningPokerView(activity)).getView();
					case 1:
						rootView = inflater.inflate(R.layout.login_main, container, false);
						return (new LoginActivity(activity, rootView)).getView();
                    case 7:
                        if (savedView == null) {
                            savedView = (new CommitExpandListView(activity)).getView();
                            return savedView;
                        }
				}

			}
			return rootView;
		}
	}

}
