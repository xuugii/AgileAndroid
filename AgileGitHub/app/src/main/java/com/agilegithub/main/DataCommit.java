package com.agilegithub.main;

import com.agilegithub.main.Data.FileGit;

import org.eclipse.egit.github.core.CommitFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Created by Uguudei on 4/21/2015.
 */
public class DataCommit {
    public String author;
    public Date date;
    public String commitMessage;
    public List<FileGit> changedFiles;
    public String changes;
    public String sha;
    public List<FileGit> conflictedFiles;
}

