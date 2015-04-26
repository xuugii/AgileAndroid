package githubnotify.githubpro.com.githubnotify;

import android.app.Activity;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import static githubnotify.githubpro.com.githubnotify.R.*;

/**
 * Created by Uguudei on 4/26/2015.
 */
public class GitHubView {
    ListView listView;
    GitHubViewListAdapter listAdapter;

    private Activity activity;
    public GitHubView(Activity activity){
        this.activity = activity;
        listView = new ListView(activity);
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        listParams.setMargins(10,10,10,10);
        listView.setLayoutParams(listParams);
        listView.setBackgroundColor(Color.GRAY);
        listAdapter = new GitHubViewListAdapter(activity);
        listView.setAdapter(listAdapter);
    }

    public ListView getListView(){
        return listView;
    }
}
