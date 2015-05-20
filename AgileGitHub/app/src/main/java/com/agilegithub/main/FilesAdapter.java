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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.agilegithub.main.Data.FileGit;

import org.eclipse.egit.github.core.CommitFile;
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


public class FilesAdapter extends BaseAdapter implements AdapterView.OnItemClickListener, Filterable {

    public static List<FileGit> filesList = new Vector<>();
    public static List<FileGit> filesListFilter;
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
    private static int filter_size = 0;
    private static boolean filter_start = false;

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if ( results != null)
                    filesList = (List<FileGit>) results.values;
                else
                    filesList = filesListFilter;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint.length() == 0){
                    filter_size = 0;
                    filter_start = false;
                    return null;
                }

                if (constraint.length() > filter_size){
                    if (!filter_start){
                        filesListFilter = new Vector<>(filesList);
                        filter_start = true;
                    }

                    filter_size = constraint.length();
                    FilterResults results = new FilterResults();
                    List<FileGit> FilteredArrayNames = new Vector<FileGit>();

                    // perform your search here using the searchConstraint String.

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < filesList.size(); i++) {
                        String dataNames = filesList.get(i).getName();
                        if (dataNames.toLowerCase().startsWith(constraint.toString()))  {
                            FilteredArrayNames.add(filesList.get(i));
                        }
                    }

                    results.count = FilteredArrayNames.size();
                    results.values = FilteredArrayNames;
                    if (MainActivity.debug)
                        Log.d("VALUES", results.values.toString());
                    return results;
                } else {
                    filesList = filesListFilter;
                    filter_size = constraint.length();
                    FilterResults results = new FilterResults();
                    List<FileGit> FilteredArrayNames = new Vector<FileGit>();

                    // perform your search here using the searchConstraint String.

                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < filesList.size(); i++) {
                        String dataNames = filesList.get(i).getName();
                        if (dataNames.toLowerCase().startsWith(constraint.toString()))  {
                            FilteredArrayNames.add(filesList.get(i));
                        }
                    }

                    results.count = FilteredArrayNames.size();
                    results.values = FilteredArrayNames;
                    Log.e("VALUES", results.values.toString());
                    return results;
                }
            }
        };

        return filter;
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
        FileGit f = filesList.get(position);
        holder.fileName.setText(f.getName());
        holder.pathView.setText("" + f.getPath());
        holder.chkbox.setChecked(f.isSelected());
        holder.chkbox.setTag(f);
        holder.chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
                if (filesList.get(position).isSelected()){
                    MainActivity.selectedFilesList.remove(filesList.get(position));
                } else {
                    MainActivity.selectedFilesList.add(filesList.get(position));
                }
                if (MainActivity.debug){
                    Log.e(TAG, "FileSelected. selectedFilesList size:  " + MainActivity.selectedFilesList.size());
                }
                filesList.get(position).performClick();

            }

        });
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
            if (filesList == null)
                return;
             getFilesFromContent(GitHubParser.gitHubParser().getRepoFiles(),filesList);
        } catch (IOException e) {
            Log.e(TAG, "updateComments failed: " + e.getMessage());
        }
        updateThread();
    }
    public static void getFilesFromContent(ArrayList<RepositoryContents> repo,  List<FileGit> list){
        for (int i = 0; i < repo.size(); i++) {
            FileGit file = new FileGit(repo.get(i).getName(), repo.get(i).getPath());
            if (!list.contains(file)){
                list.add(0,file);
            }
        }
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
