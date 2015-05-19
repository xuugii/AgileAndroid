package com.agilegithub.main;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.eclipse.egit.github.core.RepositoryContents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alexey on 04.05.2015.
 */

class Files {
    String name;
    String path;
    boolean selected = false;

    public Files(String name, String path) {
        super();
        this.name = name;
        this.path = path;
    }

    public String getName() {
        if (name == null)
            return "EMPTY";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void performClick(){
        selected = !selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

public class FilesAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    public static List<Files> filesList;
    private Activity context;
    public static String TAG = "FilesAdapter";
    public FilesAdapter fileAdapter;

    public FilesAdapter( Activity context) {
        this.context = context;
        fileAdapter = this;
        initGitHub();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckBox chk = (CheckBox) view.findViewById(R.id.chk_box);
        if (filesList.get(position).isSelected()){
            MainActivity.selectedFilesList.remove(filesList.get(position));
        } else {
            MainActivity.selectedFilesList.add(filesList.get(position));
        }
        if (MainActivity.debug){
            Log.e(TAG, "FileSelected. selectedFilesList size:  " + MainActivity.selectedFilesList.size());
        }
        filesList.get(position).performClick();
        chk.performClick();
    }

    private static class FilesHolder {
        public TextView fileName;
        public TextView pathView;
        public CheckBox chkbox;
    }

    @Override
    public int getCount() {
        if (filesList == null)
            return 0;
        return filesList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        View v = convertView;
        FilesHolder holder = new FilesHolder();
        if (convertView == null || (ViewHolder) convertView.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_select_files, parent, false);
            holder.fileName = (TextView) v.findViewById(R.id.name);
            holder.pathView = (TextView) v.findViewById(R.id.path);
            holder.chkbox = (CheckBox) v.findViewById(R.id.chk_box);
        } else {
            holder = (FilesHolder) v.getTag();
        }
        Files f = filesList.get(position);
        holder.fileName.setText(f.getName());
        holder.pathView.setText("" + f.getPath());
        holder.chkbox.setChecked(f.isSelected());
        holder.chkbox.setTag(f);
        return v;
    }


    private static final ScheduledExecutorService worker = Executors
            .newSingleThreadScheduledExecutor();

    void initGitHub(){
        Runnable task = new Runnable() {
            public void run() {

                updateComments();
            }
        };
        worker.schedule(task, 0, TimeUnit.SECONDS);
    }

    public void updateComments() {
        try {
            filesList = getFilesFromContent(GitHubParser.gitHubParser().getRepoFiles());
        } catch (IOException e) {
            Log.e(TAG, "updateComments failed: " + e.getMessage());
        }
        updateThread();
    }

    public static List<Files> getFilesFromContent(ArrayList<RepositoryContents> repo){
        List<Files> tmp = new Vector<>();
        for (int i = 0; i < repo.size(); i++) {
            Files file = new Files(repo.get(i).getName(), repo.get(i).getPath());
            tmp.add(file);
        }
        return tmp;
    }

    // update user Interface
    void updateThread() {
        Runnable task = new Runnable() {
            public void run() {
                context.runOnUiThread (new Thread(new Runnable() {
                    public void run(){

                        fileAdapter.notifyDataSetChanged();
//                        ((MainActivity)activity).mNavigationDrawerFragment.getTargetFragment().;
                    }
                }));
            }
        };
        worker.schedule(task, 0, TimeUnit.SECONDS);
    }
}
