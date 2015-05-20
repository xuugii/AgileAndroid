package com.agilegithub.main;

import android.util.Log;

import com.agilegithub.main.Data.FileGit;
import com.agilegithub.main.adapter.CommitExpandableListAdapter;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitFile;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryCommitCompare;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
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
    RepositoryService repositoryService;
    SearchRepository searchRepository;
    ContentsService contentsService;
    static GitHubParser gitHub;
    static boolean problem = false;
    public static String TAG = "GitHubParser";
    private static volatile int selfFixLogin = 0;
    public static GitHubParser gitHubParser(){
        if (gitHub == null || problem){
            gitHub =  new GitHubParser();
            if (selfFixLogin>1){
                gitHub.getGitHubParserPassword(LoginActivity.userName, LoginActivity.password, LoginActivity.repoName);
                selfFixLogin = 0;
            }
            problem = false;
            selfFixLogin++;
        }
        return gitHub;
    }

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
    private GitHubParser(){

    }
    public GitHubParser getGitHubParserToken(String token, String repoName){
        gitHub.token = token;
        gitHub.loginType = LoginType.TOKEN;
        gitHub.repoName = repoName;
        loadListInitGit();
        return gitHub;
    }

    public GitHubParser getGitHubParserPassword(String user, String password, String repoName){
        gitHub.user = user;
        gitHub.password = password;
        gitHub.loginType = LoginType.PASSWORD;
        gitHub.repoName = repoName;
        loadListInitGit();
        return gitHub;
    }

    private static void loadListInitGit(){
        Runnable expandList = new Runnable() {
            public void run() {
                gitHub.init();
                CommitExpandableListAdapter.commits = gitHub.getListCommits();
                try {
                    if (CommitExpandableListAdapter.commits == null)
                        CommitExpandableListAdapter.commits = new Vector<>();
                    CommitExpandableListAdapter.commits = gitHub.getListCommits();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < CommitExpandableListAdapter.commits.size()-1; i++) {
                    try {
                        CommitExpandableListAdapter.commits.get(i).changedFiles = gitHub.getChangedFiles(CommitExpandableListAdapter.commits.get(i+1).sha, CommitExpandableListAdapter.commits.get(i).sha);
                    } catch (IOException e) {
                        Log.e(TAG, "Error in updateThread: " + e.getMessage());
                    }
                }
            }
        };
        worker.schedule(expandList, 0, TimeUnit.SECONDS);

        Runnable fileGIT = new Runnable() {
            public void run() {
                try {
                    if (FilesAdapter.filesList == null)
                        FilesAdapter.filesList = new Vector<>();
                        FilesAdapter.getFilesFromContent(GitHubParser.gitHubParser().getRepoFiles(),FilesAdapter.filesList);
                } catch (IOException e) {
                    Log.e(TAG, "updateComments failed: " + e.getMessage());
                }
            }
        };
        worker.schedule(fileGIT, 0, TimeUnit.SECONDS);
    }

    private void init(){
        client = new GitHubClient();
        if (loginType == LoginType.TOKEN)
            client.setOAuth2Token(token);
        else
            client.setCredentials(user, password);
        service = new RepositoryService(client);
        commitService = new CommitService(client);
        repositoryService = new RepositoryService(client);
        contentsService = new ContentsService(client);
        try {
            searchRepository = service.searchRepositories(repoName).get(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            problem = true;
        }
    }

    public ArrayList<FileGit> getChangedFiles(String baseSha, String headSha) throws IOException{
        ArrayList <FileGit> tmp = new ArrayList<FileGit>();
        RepositoryCommitCompare com = commitService.compare(searchRepository, baseSha, headSha);
        for (int i = 0; i < com.getFiles().size(); i++) {
            FileGit fileGit = new FileGit(com.getFiles().get(i));
            tmp.add(fileGit);
        }
        return tmp;
    }

    public List<DataCommit> getListCommits(){
        List<DataCommit> tmp = new Vector<>();
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
                        dataCommit.sha =  list.get(i).getSha();
                        tmp.add(dataCommit);
                    }
                    return tmp;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("GitHubParser", "getListCommits()" + e.getMessage());
            problem = true;
            e.printStackTrace();
        }
        return tmp;
    }

    public ArrayList<RepositoryContents> getRepoFiles() throws IOException {
        ArrayList<RepositoryContents> tmp = new ArrayList<>();
        dirFlatten(tmp, "");
        return tmp;
    }

    int test;
    private void dirFlattenTest(List<RepositoryContents> savedArray, String dirPath) throws IOException{
        List<RepositoryContents> files;
        if (dirPath == null || dirPath == "")
            files = contentsService.getContents(searchRepository);
        else
            files = contentsService.getContents(searchRepository, dirPath);
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getType().equals(RepositoryContents.TYPE_DIR)){

            } else {
                savedArray.add(files.get(i));
            }
        }
    }

    private void dirFlatten(ArrayList<RepositoryContents> savedArray, String dirPath) throws IOException{
        List<RepositoryContents> files;
        if (dirPath == null || dirPath == "")
            files = contentsService.getContents(searchRepository);
        else
            files = contentsService.getContents(searchRepository, dirPath);
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i).getType().equals(RepositoryContents.TYPE_DIR)){
                dirFlatten(savedArray, files.get(i).getPath());
            } else {
                savedArray.add(files.get(i));
            }
        }

    }

    /**
     * Threading
     * @param args
     */
    private static final ScheduledExecutorService worker = Executors
            .newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        // TODO Auto-generated method stub
       // GitHubParser tmp = new GitHubParser("a526523aab6a8c733eb27d456b9ac33d53989d6c", "AgileAndroid");
      //  List<DataCommit> list = tmp.getListCommits();
    //    for (int i = 0; i < list.size(); i++) {
  //          System.out.println(list.get(i).author + ", message = " + list.get(i).commitMessage + ", date = " + list.get(i).date);
//        }



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
