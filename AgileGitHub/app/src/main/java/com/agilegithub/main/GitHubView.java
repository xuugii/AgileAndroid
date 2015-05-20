package com.agilegithub.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by Uguudei on 4/26/2015.
 */
public class GitHubView implements IFragmentView{
    ListView listView;
    GitHubViewListAdapter listAdapter;

    private Activity activity;

    @Override
    public View getView() {
        return listView;
    }

    public GitHubView(Activity activity){
        this.activity = activity;
        listView = new ListView(activity);
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        listParams.setMargins(10, 10, 10, 10);
        listView.setLayoutParams(listParams);
        listView.setBackgroundColor(Color.GRAY);
        listAdapter = new GitHubViewListAdapter(activity);
        listView.setAdapter(listAdapter);
    }

    public void updateSyncData(){
        if (listAdapter != null)
        listAdapter.initGitHub(0);
    }
}
