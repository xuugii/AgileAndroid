package com.agilegithub.main;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import org.eclipse.egit.github.core.RepositoryContents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SelectFiles implements IFragmentView{

    ListView lv;
    public static FilesAdapter fsAdapter;
    public static String TAG = "SelectFiles";
    public static EditText inputSearch;
    public Activity activity;
    View allView;
    public SelectFiles(final Activity activity, View view) {
        allView = view;
        inputSearch = (EditText) view.findViewById(R.id.inputSearch);
        lv = (ListView) view.findViewById(R.id.list_view);
        fsAdapter = new FilesAdapter(activity);
        lv.setAdapter(fsAdapter);
        this.activity = activity;
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                View view = activity.getCurrentFocus();
                if (view == inputSearch) {
                    fsAdapter.getFilter().filter(cs);
                  //  if (FilesAdapter.filesList.size()==(FilesAdapter.filesListFilter.size())) {
                    //    inputSearch.clearFocus();
                     //   inputSearch.clearComposingText();
                       // hideKeyboard();
                   // }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }



    @Override
    public View getView() {
        return allView;
    }

    @Override
    public void updateSyncData() {
        fsAdapter.initGitHub();
    }
}
