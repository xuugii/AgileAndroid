package com.agilegithub.main;

import android.util.Log;

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
    public static GitHubParser gitHubParser(){
        if (gitHub == null || problem){
            gitHub =  new GitHubParser();
            GitHubParser.getGitHubParserPassword(LoginActivity.userName, LoginActivity.password, LoginActivity.repoName);
            problem = false;
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
    public static GitHubParser getGitHubParserToken(String token, String repoName){
        GitHubParser git = gitHubParser();
        git.token = token;
        git.loginType = LoginType.TOKEN;
        git.repoName = repoName;
        git.init();
        gitHub = git;
        loadList();
        return gitHub;
    }

    public static GitHubParser getGitHubParserPassword(String user, String password, String repoName){
        GitHubParser git = gitHubParser();
        git.user = user;
        git.password = password;
        git.loginType = LoginType.PASSWORD;
        git.repoName = repoName;
        git.init();
        gitHub = git;
        loadList();
        return gitHub;
    }

    private static void loadList(){
        Runnable task = new Runnable() {
            public void run() {
                CommitExpandableListAdapter.commits = gitHub.getListCommits();
                try {
                    FilesAdapter.filesList = FilesAdapter.getFilesFromContent(gitHub.getRepoFiles());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.schedule(task, 0, TimeUnit.SECONDS);
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

    public ArrayList<CommitFile> getChangedFiles(String baseSha, String headSha) throws IOException{
        ArrayList <CommitFile> tmp = new ArrayList<CommitFile>();
        RepositoryCommitCompare com = commitService.compare(searchRepository, baseSha, headSha);
        tmp.addAll(com.getFiles());
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
        System.out.println("Size of the files: " + tmp.size());
        return tmp;
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

    public ArrayList<Files> getListFiles(String sha){
        try {
            ArrayList<Files> tmp = new ArrayList<Files>();
            for (Repository repo : service.getRepositories()){
                if (repo.getName().equals(repoName)) {
                    List<RepositoryCommit> lst = commitService.getCommits(repo);
                    int size = lst.size();
                    for (int i = 0; i < size; i++) {
                        Commit commit = lst.get(i).getCommit();
                        String url = commit.getUrl();
                        url = url.substring(url.lastIndexOf('/') + 1);
                        if (url.equals(sha)){

                            List<CommitFile> modifiedFiles = commitService.getCommit(repo, sha).getFiles();
                            for (CommitFile file: modifiedFiles)
                            {
                                tmp.add(new Files(file.getFilename(), file.getRawUrl()));
                            }

                            // TODO: get project files here instead of getting commit-related files
                            // Note that we only need files prior to a selected commit
                            // So we shall probably use commit's SHA in "getAllFiles" method

                            break;
                        }
                    }
                    return tmp;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            problem = true;
            e.printStackTrace();
        }
        return null;
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
