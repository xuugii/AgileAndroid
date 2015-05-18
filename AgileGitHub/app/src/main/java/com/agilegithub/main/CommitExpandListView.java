package com.agilegithub.main;

import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.agilegithub.main.adapter.CommitExpandableListAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Uguudei on 2015-05-16.
 */
public class CommitExpandListView implements IFragmentView{


    private Activity activity;

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> laptopCollection;
    ExpandableListView expListView;
    private int lastExpandedPosition = -1;
    public static String TAG = "CommitExpandListView";


    @Override
    public View getView() {
        if (CommitExpandableListAdapter.listAdapter != null)
            CommitExpandableListAdapter.listAdapter.updateComments();
        return expListView;
    }

    public CommitExpandListView(final Activity activity){
        this.activity = activity;

        expListView = new ExpandableListView(activity);
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        expListView.setLayoutParams(listParams);
        final CommitExpandableListAdapter expListAdapter = new CommitExpandableListAdapter(activity);
        expListView.setAdapter(expListAdapter);
        expListView.setLongClickable(true);
        expListView.setClickable(true);
        // Close the expanded list
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                if (MainActivity.debug)
                    Log.d(TAG, "onChildClick is clicked : " + groupPosition);
                expListView.collapseGroup(groupPosition);
                return true;
            }
        });

        expListView.setOnItemClickListener(new ExpandableListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                Log.d(TAG, "setOnItemClickListener is clicked : " + l);
            }
        });

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (MainActivity.debug)
                    Log.d(TAG, "setOnGroupClickListener is clicked : " + groupPosition);
                expListView.expandGroup(groupPosition);
                return true;
            }
        });

        expListView.setOnItemLongClickListener(new ExpandableListView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                if (MainActivity.debug)
                    Log.d(TAG, "setOnItemLongClickListener is clicked : " + index);
                Intent display = new Intent(MainActivity.activity, DisplayWebView.class);
                String all = "";
                if (CommitExpandableListAdapter.commits.get(index) != null){

                    DataCommit data = CommitExpandableListAdapter.commits.get(index);
                    for (int i = 0; i < data.changedFiles.size(); i++) {
                        all = all + "/n" + data.changedFiles.get(i).getPatch();
                    }
                }
                expListView.expandGroup(index);
                display.putExtra("html", ""+ all);
                MainActivity.activity.startActivity(display);
                return false;
            }
        });
    }
}
