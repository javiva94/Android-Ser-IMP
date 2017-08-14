package com.example.iaeste.general.View;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iaeste.general.MainActivity;
import com.example.iaeste.general.MapsActivity;
import com.example.iaeste.general.Model.Task;
import com.example.iaeste.general.R;

import java.util.List;

/**
 * Created by franc on 14/8/2017.
 */

public abstract class TaskViewAdapter extends ArrayAdapter<Task> {

    public TaskViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Task> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (null == v) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflateView(inflater);
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

    public abstract View inflateView(LayoutInflater inflater);
}
