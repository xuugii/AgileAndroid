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
    static CommitExpandableListAdapter expListAdapter = null;


    @Override
    public View getView() {
        return expListView;
    }
    static private volatile int previousGroupPosition = -1;

    @Override
    public void updateSyncData() {
        if (expListAdapter != null)
            expListAdapter.initGitHub();
    }


    public CommitExpandListView(final Activity activity){
        this.activity = activity;

        expListView = new ExpandableListView(activity);
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        expListView.setLayoutParams(listParams);
        expListAdapter = new CommitExpandableListAdapter(activity);
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
                if (expListView.isGroupExpanded(groupPosition)){
                    if (groupPosition == previousGroupPosition){
                        expListView.collapseGroup(groupPosition);
                    } else {
                        expListView.expandGroup(groupPosition);
                    }
                } else {
                    expListView.expandGroup(groupPosition);
                }
                previousGroupPosition = groupPosition;
                return true;
            }
        });

        expListView.setOnItemLongClickListener(new ExpandableListView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                if (MainActivity.debug)
                    Log.d(TAG, "setOnItemLongClickListener is clicked : " + index);
                if (CommitExpandableListAdapter.commits == null || CommitExpandableListAdapter.commits.size() < index)
                    return false;
                Intent display = new Intent(MainActivity.activity, DisplayWebView.class);
                String all = "";
                if (CommitExpandableListAdapter.commits.get(index) != null){

                    DataCommit data = CommitExpandableListAdapter.commits.get(index);
                    for (int i = 0; i < data.changedFiles.size(); i++) {
                        all = all + "/n" + data.changedFiles.get(i).getPatch();
                    }
                }
                display.putExtra("html", ""+ all);
                MainActivity.activity.startActivity(display);
                return false;
            }
        });

        expListView.setOnItemLongClickListener(new ExpandableListView.OnItemLongClickListener(){
            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long id) {
                if (MainActivity.debug)
                    Log.d(TAG, "setOnItemLongClickListener is clicked : " + index);
                if (CommitExpandableListAdapter.commits == null ||
                        CommitExpandableListAdapter.commits.size() < index ||
                        CommitExpandableListAdapter.commits.get(index).changedFiles == null)
                    return false;

                int itemType = ExpandableListView.getPackedPositionType(id);
                int childPosition = -1;
                int groupPosition = -1;
                if ( itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    childPosition = ExpandableListView.getPackedPositionChild(id);
                    groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    Intent display = new Intent(MainActivity.activity, DisplayWebView.class);
                    String all = "";
                    if (CommitExpandableListAdapter.commits.get(groupPosition) != null){

                        DataCommit data = CommitExpandableListAdapter.commits.get(groupPosition);
                        if (data.changedFiles.size()>childPosition){
                            all = data.changedFiles.get(childPosition).getPatch();
                            display.putExtra("html", ""+ all);
                            MainActivity.activity.startActivity(display);
                            return true;
                        }
                    }
                    return true; //true if we consumed the click, false if not

                } else if(itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    Intent display = new Intent(MainActivity.activity, DisplayWebView.class);
                    String all = "";
                    if (CommitExpandableListAdapter.commits.get(groupPosition) != null){

                        DataCommit data = CommitExpandableListAdapter.commits.get(groupPosition);
                        for (int i = 0; i < data.changedFiles.size(); i++) {
                            all = all + "/n" + data.changedFiles.get(i).getPatch();
                        }
                    }
                    display.putExtra("html", ""+ all);
                    MainActivity.activity.startActivity(display);
                    return true; //true if we consumed the click, false if not

                } else {
                    // null item; we don't consume the click
                    return true;
                }
            }
        });

        updateSyncData();
    }
}
