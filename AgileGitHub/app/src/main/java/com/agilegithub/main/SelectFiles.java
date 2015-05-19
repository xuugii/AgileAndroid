package com.agilegithub.main;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import org.eclipse.egit.github.core.RepositoryContents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SelectFiles {

    ListView lv;
    FilesAdapter fsAdapter;
    public static String TAG = "SelectFiles";
    public SelectFiles(Activity activity) {

        lv = new ListView(activity);
        fsAdapter = new FilesAdapter(activity);
        lv.setAdapter(fsAdapter);
    }


    public ListView getListView(){
        return lv;
    }
}
