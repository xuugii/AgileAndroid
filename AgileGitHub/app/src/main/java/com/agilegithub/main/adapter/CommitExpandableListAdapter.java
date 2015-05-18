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

    import android.app.Activity;
    import android.content.Context;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseExpandableListAdapter;
    import android.widget.ImageView;
    import android.widget.TextView;

    import com.agilegithub.main.DataCommit;
    import com.agilegithub.main.GitHubParser;
    import com.agilegithub.main.LoginActivity;
    import com.agilegithub.main.R;

    import org.eclipse.egit.github.core.CommitFile;

public class CommitExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    public static List<DataCommit> commits;
    public String TAG = "CommitExpandableListAdapter";
    public static GitHubParser gitHub;
    public static CommitExpandableListAdapter listAdapter;
    public static int lastExpandedPosition;


    public CommitExpandableListAdapter(Activity context, List<DataCommit> commits) {
        initGitHub();
        this.context = context;
        this.commits = commits;
        listAdapter = this;
        listAdapter.
        commits = new ArrayList<>();
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
        if (!commits.isEmpty() && !commits.get(groupPosition).changedFiles.isEmpty()){
            if (convertView == null || (ViewChildHolder) convertView.getTag() == null) {
                holder = new ViewChildHolder();
                LayoutInflater amInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = amInflater.inflate(R.layout.commit_child_item_xml, null);
                holder.stringName = (TextView) convertView.findViewById(R.id.ChildListPath);
                holder.changes = (TextView) convertView.findViewById(R.id.ChildListChanges);
                convertView.setTag(holder);
            } else  {
                holder = (ViewChildHolder) convertView.getTag();
            }
            CommitFile file = commits.get(groupPosition).changedFiles.get(childPosition);
            holder.stringName.setText(file.getFilename());
            holder.changes.setText(Integer.toString(file.getChanges()));
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
            holder.stringName.setText(commits.get(groupPosition).author);
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

    void initGitHub(){
        Runnable task = new Runnable() {
            public void run() {
                if (LoginActivity.password.length()!=0){
                    gitHub = new GitHubParser(LoginActivity.userName,LoginActivity.password, LoginActivity.repoName);
                } else {
                    if (LoginActivity.token.length() == 0){
                        //todo exit?
                    } else {
                        gitHub = new GitHubParser(LoginActivity.token, LoginActivity.repoName);
                    }
                }
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
                        commits.get(i).changedFiles = gitHub.getChangedFiles(commits.get(i+1).sha, commits.get(i).sha);
                    } catch (IOException e) {
                        Log.e(TAG, "Error in updateThread: " + e.getMessage());
                    }
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

    public void updateComments(){
        if (gitHub == null)
            return;
        commits = gitHub.getListCommits();
        updateThread();
    }

    class ViewHolder {
        TextView stringName;
        TextView message;
    }

    class ViewChildHolder{
        TextView stringName;
        TextView changes;
    }
}

