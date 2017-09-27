package com.example.iaeste.general.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.iaeste.general.Model.Task;
import com.example.iaeste.general.R;

import java.util.List;

/**
 * Created by iaeste on 03/08/2017.
 */

public class ListViewAdapter extends TaskViewAdapter {
    public ListViewAdapter( Context context, int resource, List<Task> objects) {
        super(context, resource, objects);
    }

    @Override
    public View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.list_item, null);
    }
}
