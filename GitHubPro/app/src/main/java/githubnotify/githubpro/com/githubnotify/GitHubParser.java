package githubnotify.githubpro.com.githubnotify;

import android.util.Log;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Uguudei on 4/21/2015.
 */
public class GitHubParser {
    private String password;
    private String token;
    private LoginType loginType;
    private String repoName;
    GitHubClient client;
    RepositoryService service;
    CommitService commitService;
    //TODO: check user name!
    public String userName = "xuugii";

    public LoginType getLoginType() {
        return loginType;
    }

    private String user;

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public static enum LoginType{TOKEN,PASSWORD,NONE};

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public GitHubParser(String token, String repoName){
        this.token = token;
        this.loginType = LoginType.TOKEN;
        this.repoName = repoName;
        init();
    }

    public GitHubParser(String user, String password, String repoName){
        this.user = user;
        this.password = password;
        this.loginType = LoginType.PASSWORD;
        this.repoName = repoName;
        init();
    }

    private void init(){
        client = new GitHubClient();
        if (loginType == LoginType.TOKEN)
            client.setOAuth2Token(token);
        else
            client.setCredentials(user, password);
        service = new RepositoryService(client);
        commitService = new CommitService(client);
    }

    public List<DataCommit> getListCommits(){
        List<DataCommit> tmp = new ArrayList<>();
        try {
            for (Repository repo : service.getRepositories()){
                if (repo.getName().equals(repoName)) {
                    List<RepositoryCommit> list = commitService.getCommits(repo);
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        DataCommit dataCommit = new DataCommit();
                        Commit commit = list.get(i).getCommit();
                        dataCommit.author = commit.getCommitter().getEmail();
                        dataCommit.date = commit.getCommitter().getDate();
                        dataCommit.commitMessage = commit.getMessage();
                        tmp.add(dataCommit);
                    }
                    return tmp;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("GitHubParser", "getListCommits()" + e.getMessage());
            e.printStackTrace();
        }
        return tmp;
    }

    public List<File> getRepoFiles(){
        try {
            for (Repository repo : service.getRepositories()){
                if (repo.getName().equals(repoName)) {
                    Repository d = repo.getSource();
                    List<RepositoryCommit> list = commitService.getCommits(repo);
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        DataCommit dataCommit = new DataCommit();
                        Commit commit = list.get(i).getCommit();
                        commit.getTree();
                        dataCommit.author = commit.getCommitter().getEmail();
                        dataCommit.date = commit.getCommitter().getDate();
                        dataCommit.commitMessage = commit.getMessage();
//                        tmp.add(dataCommit);
                    }
                    return null;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }



    public static void main(String[] args) {
        // TODO Auto-generated method stub
        GitHubParser tmp = new GitHubParser("a526523aab6a8c733eb27d456b9ac33d53989d6c", "AgileAndroid");
        List<DataCommit> list = tmp.getListCommits();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).author + ", message = " + list.get(i).commitMessage + ", date = " + list.get(i).date);
        }



        /*GitHubClient client = new GitHubClient();
        client.setOAuth2Token();
        RepositoryService service = new RepositoryService(client);
        CommitService commitService = new CommitService(client);

        try {
            for (Repository repo : service.getRepositories()){

                System.out.println(repo.getName() + " String: " );
                if (repo.getName().equals("AgileAndroid")){
                    for (int i = 0; i < commitService.getCommits(repo).size(); i++) {
                        System.out.println(commitService.getCommits(repo).get(i).getCommit().getMessage());
                        System.out.println(commitService.getCommits(repo).get(i).getCommit().getCommitter().getEmail());
                        System.out.println(commitService.getCommits(repo).get(i).getCommit().getCommitter().getDate());
                        System.out.println();
                        System.out.println();
                    }
                }
            }
        } catch (IOException e) {
             TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }
}
