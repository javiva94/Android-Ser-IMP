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

import com.example.iaeste.general.Model.MyGroup;
import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 6/9/2017.
 */

public class ListSelectionViewAdapter<T> extends ArrayAdapter<T> {

    private boolean[] itemSelection;

     private List<T> itemSelected;

    public ListSelectionViewAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (null == v) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =  inflater.inflate(R.layout.user_list_item2, null);
        }
        T T = getItem(position);
        if(T instanceof MyUser){
            TextView userDisplayName = (TextView) v.findViewById(R.id.txtDisplayUser);
            MyUser myUser = (MyUser) T;
            userDisplayName.setText(myUser.getDisplayName());
        }
        if(T instanceof MyGroup){
            TextView userDisplayName = (TextView) v.findViewById(R.id.txtDisplayUser);
            MyGroup myGroup = (MyGroup) T;
            userDisplayName.setText(myGroup.getDisplayName());
        }


        itemSelection = new boolean[super.getCount()];
        for (int i=0; i < super.getCount(); i++){
            itemSelection[i] = false;
        }

        final CheckBox selectedCheckBox = (CheckBox) v.findViewById(R.id.selectedCheckBox);
        selectedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedCheckBox.isChecked()) {
                    itemSelection[position] = true;
                }else{
                    itemSelection[position] = false;
                }
            }
        });

        return v;
    }

    @Nullable
    @Override
    public T getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void add(@Nullable T object) {
        super.add(object);
    }

    public List<T> getItemSelected() {
        itemSelected = new ArrayList<>();
        for(int i=0; i<itemSelection.length; i++){
            if(itemSelection[i] == true)
                itemSelected.add(super.getItem(i));
        }
        return itemSelected;
    }
}
