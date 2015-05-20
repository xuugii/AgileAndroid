package com.agilegithub.main.adapter;

/**
 * Created by Uguudei on 2015-05-16.
 */

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.concurrent.Executors;
    import java.util.concurrent.ScheduledExecutorService;
    import java.util.concurrent.TimeUnit;
    import java.util.regex.Matcher;
    import java.util.regex.Pattern;

    import android.app.Activity;
    import android.content.Context;
    import android.provider.MediaStore;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseExpandableListAdapter;
    import android.widget.TextView;

    import com.agilegithub.main.Data.FileGit;
    import com.agilegithub.main.DataCommit;
    import com.agilegithub.main.GitHubParser;
    import com.agilegithub.main.R;

    import org.eclipse.egit.github.core.CommitFile;

public class CommitExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    public static List<DataCommit> commits;
    public String TAG = "CommitExpandableListAdapter";
    public static CommitExpandableListAdapter listAdapter;
    public static int lastExpandedPosition;
    public static volatile int latestChanges = 0;


    public CommitExpandableListAdapter(Activity context, List<DataCommit> commits) {
        initGitHub();
        this.context = context;
        this.commits = commits;
        listAdapter = this;
        listAdapter.commits = new ArrayList<>();
    }

    public CommitExpandableListAdapter(Activity context) {
        initGitHub();
        this.context = context;
        listAdapter = this;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return commits.get(groupPosition).changedFiles.get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ViewChildHolder holder;
        if (commits != null && commits.get(groupPosition).changedFiles != null) {

            if (!commits.isEmpty() &&
                    !commits.get(groupPosition).changedFiles.isEmpty()) {
                if (convertView == null || (ViewChildHolder) convertView.getTag() == null) {
                    holder = new ViewChildHolder();
                    LayoutInflater amInflater = (LayoutInflater) context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = amInflater.inflate(R.layout.commit_child_item_xml, null);
                    holder.pathName = (TextView) convertView.findViewById(R.id.ChildListPath);
                    holder.changes = (TextView) convertView.findViewById(R.id.ChildListChanges);
                    holder.name = (TextView) convertView.findViewById(R.id.ChildListName);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewChildHolder) convertView.getTag();
                }
                FileGit file = commits.get(groupPosition).changedFiles.get(childPosition);
                holder.pathName.setText(file.getFilename());
                holder.changes.setText(Integer.toString(file.getChanges()));
                holder.name.setText(FileGit.getNameFromPath(file.getFilename()));
            }
        }
        return convertView;
    }



    public int getChildrenCount(int groupPosition) {
        if (commits!= null && commits.get(groupPosition).changedFiles != null)
            return commits.get(groupPosition).changedFiles.size();
        return 0;
    }

    public Object getGroup(int groupPosition) {
        return commits.get(groupPosition);
    }

    public int getGroupCount() {
        if (commits == null)
            return 0;
        return commits.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (!commits.isEmpty()){
            if (convertView == null || (ViewHolder) convertView.getTag() == null) {
                holder = new ViewHolder();
                LayoutInflater amInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = amInflater.inflate(R.layout.git_notify_list, null);
                holder.stringName = (TextView) convertView.findViewById(R.id.gitHubViewName);
                holder.message = (TextView) convertView.findViewById(R.id.gitHubViewComment);
                convertView.setTag(holder);
            } else  {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.message.setText(commits.get(groupPosition).commitMessage);
            holder.stringName.setText(commits.get(groupPosition).date.toString());
        }
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * Treading update GUI
     */

    private static final ScheduledExecutorService worker = Executors
            .newSingleThreadScheduledExecutor();

    public void initGitHub(){
        Runnable task = new Runnable() {
            public void run() {
                updateComments();
            }
        };
        worker.schedule(task, 0, TimeUnit.SECONDS);
    }

    // update user Interface
    void updateThread() {
        Runnable task = new Runnable() {
            public void run() {
                context.runOnUiThread (new Thread(new Runnable() {
                    public void run(){

                        listAdapter.notifyDataSetChanged();
//                        ((MainActivity)activity).mNavigationDrawerFragment.getTargetFragment().;
                    }
                }));
            }
        };
        worker.schedule(task, 0, TimeUnit.SECONDS);

        Runnable task1 = new Runnable() {
            public void run() {
                for (int i = 0; i < commits.size()-1; i++) {
                    try {
                        commits.get(i).changedFiles = GitHubParser.gitHubParser().getChangedFiles(commits.get(i+1).sha, commits.get(i).sha);
                    } catch (IOException e) {
                        Log.e(TAG, "Error in updateThread: " + e.getMessage());
                    }
                }
                if (commits.get(0).changedFiles != null) {
                    int changesTotal = 0;
                    List<FileGit> com = commits.get(0).changedFiles;
                    for (int i = 0; i < com.size(); i++) {
                        changesTotal = com.get(i).getChanges() + changesTotal;
                    }
                    latestChanges = changesTotal;
                }
                context.runOnUiThread (new Thread(new Runnable() {
                    public void run(){
                        listAdapter.notifyDataSetChanged();
                    }
                }));
            }
        };
        worker.schedule(task1, 0, TimeUnit.SECONDS);
    }


    private void updateComments(){
        if (GitHubParser.gitHubParser() == null)
            return;
        commits = GitHubParser.gitHubParser().getListCommits();
        updateThread();
    }

    class ViewHolder {
        TextView stringName;
        TextView message;
    }

    class ViewChildHolder{
        TextView pathName;
        TextView changes;
        TextView name;
    }
}

