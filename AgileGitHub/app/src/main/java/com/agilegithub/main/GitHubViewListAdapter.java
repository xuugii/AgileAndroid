package com.agilegithub.main;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.agilegithub.main.adapter.CommitExpandableListAdapter;

import org.eclipse.egit.github.core.CommitFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Uguudei on 4/26/2015.
 */
public class GitHubViewListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    public static List<DataCommit> conflicted;
    private Activity activity;
    public static GitHubViewListAdapter listAdapter;
    public String TAG = "GitHubViewListAdapter";

    public static String sha;

     public void setSha(String sha_){
        sha = sha_;
    }

    public GitHubViewListAdapter(Activity activity) {
        conflicted = new Vector<>();
        this.activity = activity;
        listAdapter = this;
        mInflater = (LayoutInflater) (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        initGitHub(0);

    }

    public void updateComments(){
        updateThread();
    }

    public int getCount(){
        if (conflicted == null)
            return 0;
        return conflicted.size(); // 0 is for adding comments;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public Activity getActivity(){
        return activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
            if (!conflicted.isEmpty()){
                if (convertView == null || (ViewHolder) convertView.getTag() == null) {
                    holder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.git_notify_list, null);
                    holder.stringName = (TextView) convertView.findViewById(R.id.gitHubViewName);
                    holder.message = (TextView) convertView.findViewById(R.id.gitHubViewComment);
                    convertView.setTag(holder);
                } else  {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.message.setText(conflicted.get(position).commitMessage);
                holder.stringName.setText(conflicted.get(position).date.toString());
            }
        return convertView;
    }

    private static final ScheduledExecutorService worker = Executors
            .newSingleThreadScheduledExecutor();

    void initGitHub(final int wait){
        Runnable task = new Runnable() {
            public void run() {
                Log.e(TAG, "initGitHub is being called, conflict size : " + conflicted.size());
                if (CommitExpandableListAdapter.commits == null ||
                        CommitExpandableListAdapter.commits.size()== 0 ||
                        CommitExpandableListAdapter.commits.get(0).changedFiles == null ){
                    initGitHub(1+wait);
                    return;
                } else {
                    for (int i = 0; i < CommitExpandableListAdapter.commits.size()-1; i++) {
                        for (int j = 0; j < MainActivity.selectedFilesList.size(); j++) {
                            if (CommitExpandableListAdapter.commits.get(i).changedFiles.contains(MainActivity.selectedFilesList.get(j))){
                                if (!conflicted.contains(CommitExpandableListAdapter.commits.get(i)))
                                    conflicted.add(CommitExpandableListAdapter.commits.get(i));
                                break;
                            }
                            if (i == MainActivity.selectedFilesList.size()-1 && !conflicted.isEmpty()){
                                try {
                                    if (conflicted.contains(CommitExpandableListAdapter.commits.get(i)))
                                        conflicted.remove(CommitExpandableListAdapter.commits.get(i));
                                } catch (Exception e){

                                }
                            }

                        }

                    }
                }
                Log.e(TAG, "initGitHub is calledEnd : " + conflicted.size());
                updateComments();
            }
        };
        worker.schedule(task, wait, TimeUnit.SECONDS);
    }

    // update user Interface
    void updateThread() {
        Runnable task = new Runnable() {
            public void run() {
                activity.runOnUiThread (new Thread(new Runnable() {
                    public void run(){
                    try {
                        listAdapter.notifyDataSetChanged();
                    } catch (Exception e){
                        updateThread();
                    }
//                        ((MainActivity)activity).mNavigationDrawerFragment.getTargetFragment().;
                    }
                }));
            }
        };
        worker.schedule(task, 0, TimeUnit.SECONDS);
    }
}

class ViewHolder {
    TextView stringName;
    TextView message;
}

class CommitLine {
    public String commentID;
    public String comment;
    public String email;
    public String createDate;
}