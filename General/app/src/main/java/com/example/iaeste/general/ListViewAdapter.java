package com.example.iaeste.general;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iaeste.general.Model.Task;

import java.util.List;

/**
 * Created by iaeste on 03/08/2017.
 */

public class ListViewAdapter extends ArrayAdapter<Task> {
    public ListViewAdapter( Context context, int resource, List<Task> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (null == v) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }
        Task task = getItem(position);
        ImageView img = (ImageView) v.findViewById(R.id.imageView);
        TextView txtTitle = (TextView) v.findViewById(R.id.txtTitle);
        TextView txtDescription = (TextView) v.findViewById(R.id.txtDescription);

        txtTitle.setText(task.getTitle());
        txtDescription.setText(task.getDescription());

        return v;
    }

    @Override
    public void add(@Nullable Task object) {
        super.add(object);
    }
}
