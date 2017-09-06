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
 * Created by franc on 6/9/2017.
 */

public class UsersSelectionViewAdapter extends ArrayAdapter<MyUser> {

    private boolean[] usersSelected;

    public UsersSelectionViewAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (null == v) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =  inflater.inflate(R.layout.user_list_item2, null);
        }
        MyUser myUser = getItem(position);
        TextView userDisplayName = (TextView) v.findViewById(R.id.txtDisplayUser);
        userDisplayName.setText(myUser.getDisplayName());

        usersSelected = new boolean[super.getCount()];
        for (int i=0; i < super.getCount(); i++){
            usersSelected[i] = false;
        }

        final CheckBox selectedCheckBox = (CheckBox) v.findViewById(R.id.selectedCheckBox);
        selectedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedCheckBox.isChecked()) {
                    usersSelected[position] = true;
                }else{
                    usersSelected[position] = false;
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

    public boolean[] getUsersSelected() {
        return usersSelected;
    }

}
