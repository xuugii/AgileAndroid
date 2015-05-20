package com.agilegithub.main.Data;

import org.eclipse.egit.github.core.CommitFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Uguudei on 2015-05-20.
 */
public class FileGit extends CommitFile {
    public String name;
    public String path;
    boolean selected = false;

    public FileGit(String name, String path) {
        super();
        this.setName(name);
        this.setPath(path);
        this.name = name;
        this.path = path;
    }

    public FileGit(CommitFile file){
        this.setSha(file.getSha());
        this.setAdditions(file.getAdditions());
        this.setBlobUrl(file.getBlobUrl());
        this.setChanges(file.getChanges());
        this.setFilename(file.getFilename());
        this.setPatch(file.getPatch());
        this.setRawUrl(file.getRawUrl());
        this.setStatus(file.getStatus());
        this.setDeletions(file.getDeletions());
        this.setAdditions(file.getAdditions());
        this.setName(getNameFromPath(file.getFilename()));
        this.setPath(file.getFilename());

    }

    public static String getNameFromPath(String file){
        String tmp = "";
        try {
            return file.substring(file.lastIndexOf("/")+1, file.lastIndexOf("."));
        } catch (Exception e){
            return file;
        }
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

    @Override
    public boolean equals(Object object)
    {
        boolean result = false;

        if (object != null && object instanceof FileGit)
        {
            result = this.path.equals (((FileGit) object).path);
        }

        return result;
    }
}
