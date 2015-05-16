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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sha = "db9871c2bdd9c787003b723fa300c892cf5b7680";    // head

                // TODO: this commit SHA parameter has to be gotten from listview element (in git_notify_list)
                // TODO: for this purpose we might add SHA field to ViewHolder class to retrieve it here

                listAdapter.setSha(sha);
                listAdapter.CommitFiles();
                if (listAdapter.getCommitFiles() != null) {
                    MainActivity.selectedFilesList = listAdapter.getCommitFiles();
                }

                // TODO: go to Select Files activity once the list of commit files is updated
            }
        });
    }
}
