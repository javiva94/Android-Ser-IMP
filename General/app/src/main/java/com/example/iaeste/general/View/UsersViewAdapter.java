package com.example.iaeste.general.View;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iaeste.general.Model.Task;
import com.example.iaeste.general.Model.User;
import com.example.iaeste.general.R;

import java.util.List;

/**
 * Created by franc on 31/8/2017.
 */

public class UsersViewAdapter extends ArrayAdapter<User> {

    public UsersViewAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (null == v) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =  inflater.inflate(R.layout.user_list_item, null);
        }
        User user = getItem(position);
        TextView userDisplayName = (TextView) v.findViewById(R.id.txtDisplayUser);
        userDisplayName.setText(user.getDisplayName());

        return v;
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void add(@Nullable User object) {
        super.add(object);
    }

}
