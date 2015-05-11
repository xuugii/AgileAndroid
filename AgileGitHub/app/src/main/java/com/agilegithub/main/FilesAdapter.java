package com.agilegithub.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alexey on 04.05.2015.
 */

class Files {
    String name;
    String path;
    boolean selected = false;

    public Files(String name, String path) {
        super();
        this.name = name;
        this.path = path;
    }

    public String getName() {
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

public class FilesAdapter extends ArrayAdapter<Files> {

    private List<Files> filesList;
    private Context context;

    public FilesAdapter(List<Files> filesList, Context context) {
        super(context, R.layout.single_listview_item, filesList);
        this.filesList = filesList;
        this.context = context;
    }

    private static class FilesHolder {
        public TextView fileName;
        public TextView pathView;
        public CheckBox chkbox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View v = convertView;
        FilesHolder holder = new FilesHolder();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_select_files, parent, false);
            holder.fileName = (TextView) v.findViewById(R.id.name);
            holder.pathView = (TextView) v.findViewById(R.id.path);
            holder.chkbox = (CheckBox) v.findViewById(R.id.chk_box);
            holder.chkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    filesList.get(position).selected = !filesList.get(position).selected;
                }
            });
        } else {
            holder = (FilesHolder) v.getTag();
        }
        Files f = filesList.get(position);
        holder.fileName.setText(f.getName());
        holder.pathView.setText("" + f.getPath());
        holder.chkbox.setChecked(f.isSelected());
        holder.chkbox.setTag(f);
        return v;
    }
}
