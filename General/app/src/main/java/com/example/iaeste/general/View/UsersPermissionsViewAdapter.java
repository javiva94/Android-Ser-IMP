package com.example.iaeste.general.View;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.R;

/**
 * Created by franc on 31/8/2017.
 */

public class UsersPermissionsViewAdapter extends ArrayAdapter<MyUser> {

    private boolean[] readPermissionCheck;
    private boolean[] writePermissionCheck;

    public UsersPermissionsViewAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (null == v) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =  inflater.inflate(R.layout.user_list_item, null);
        }
        MyUser myUser = getItem(position);
        TextView userDisplayName = (TextView) v.findViewById(R.id.txtDisplayUser);
        userDisplayName.setText(myUser.getDisplayName());

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
    public MyUser getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void add(@Nullable MyUser object) {
        super.add(object);
    }

    public boolean[] getReadPermissionCheck() {
        return readPermissionCheck;
    }

    public boolean[] getWritePermissionCheck() {
        return writePermissionCheck;
    }
}
