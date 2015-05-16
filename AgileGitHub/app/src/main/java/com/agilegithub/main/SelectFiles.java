package com.agilegithub.main;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectFiles {

    ListView lv;
    ArrayList<Files> filesList;
    FilesAdapter fsAdapter;

    public SelectFiles(Activity activity, ArrayList<Files> filesList_) {

        lv = new ListView(activity);

        if (!filesList_.isEmpty()) {
            filesList = filesList_;
            fsAdapter = new FilesAdapter(filesList, activity);

            lv.setAdapter(fsAdapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CheckBox chk = (CheckBox) view.findViewById(R.id.chk_box);
                    chk.performClick();
                }
            });
        }
    }

    public ListView getListView(){
        return lv;
    }
}
