package com.example.iaeste.general.View;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.iaeste.general.Model.Task;
import com.example.iaeste.general.Model.User;
import com.example.iaeste.general.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 31/8/2017.
 */

public class UsersViewAdapter extends ArrayAdapter<User> {

    private boolean[] readPermissionCheck;
    private boolean[] writePermissionCheck;

    public UsersViewAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (null == v) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =  inflater.inflate(R.layout.user_list_item, null);
        }
        User user = getItem(position);
        TextView userDisplayName = (TextView) v.findViewById(R.id.txtDisplayUser);
        userDisplayName.setText(user.getDisplayName());

        readPermissionCheck = new boolean[super.getCount()];
        writePermissionCheck = new boolean[super.getCount()];
        for (int i=0; i < super.getCount(); i++){
            readPermissionCheck[i] = false;
            writePermissionCheck[i] = false;
        }

        final CheckBox readCheckBox = (CheckBox) v.findViewById(R.id.readCheckBox);
        readCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(readCheckBox.isChecked()) {
                    readPermissionCheck[position] = true;
                }else{
                    readPermissionCheck[position] = false;
                }
            }
        });

        final CheckBox writeCheckBox = (CheckBox) v.findViewById(R.id.writeCheckBox);
        writeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(writeCheckBox.isChecked()){
                    writePermissionCheck[position] = true;
                }else{
                    writePermissionCheck[position] = false;
                }
            }
        });

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

    public boolean[] getReadPermissionCheck() {
        return readPermissionCheck;
    }

    public boolean[] getWritePermissionCheck() {
        return writePermissionCheck;
    }
}
