package githubnotify.githubpro.com.githubnotify;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Uguudei on 4/26/2015.
 */
public class GitHubViewListAdapter extends BaseAdapter{
    public static GitHubParser gitHub;
    private LayoutInflater mInflater;
    public List<DataCommit> myItems;
    private Activity activity;
    private GitHubViewListAdapter listAdapter;
    public GitHubViewListAdapter(Activity activity) {
        initGitHub();
        this.activity = activity;
        listAdapter = this;
        mInflater = (LayoutInflater) (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE));

    }

    public void updateComments(){
        myItems = gitHub.getListCommits();
        updateThread();
    }

    public int getCount(){
        if (myItems == null)
            return 0;
        return myItems.size(); // 0 is for adding comments;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public Activity getActivity(){
        return activity;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
            if (!myItems.isEmpty()){
                if (convertView == null || (ViewHolder) convertView.getTag() == null) {
                    holder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.git_notify_list, null);
                    holder.stringName = (TextView) convertView.findViewById(R.id.gitHubViewName);
                    holder.message = (TextView) convertView.findViewById(R.id.gitHubViewComment);
                    convertView.setTag(holder);
                } else  {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.message.setText(myItems.get(position).commitMessage);
                holder.stringName.setText(myItems.get(position).author);
            }
        return convertView;
    }

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

    void updateThread() {
        Runnable task = new Runnable() {
            public void run() {
                activity.runOnUiThread (new Thread(new Runnable() {
                    public void run(){

                        listAdapter.notifyDataSetChanged();
//                        ((MainActivity)activity).mNavigationDrawerFragment.getTargetFragment().;
                    }
                }));
            }
        };
        worker.schedule(task, 0, TimeUnit.SECONDS);
    }
}

class ViewHolder {
    TextView stringName;
    TextView message;
}

class CommitLine {
    public String commentID;
    public String comment;
    public String email;
    public String createDate;
}