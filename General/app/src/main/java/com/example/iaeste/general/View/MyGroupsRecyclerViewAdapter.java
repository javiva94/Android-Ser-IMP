package com.example.iaeste.general.View;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.iaeste.general.EditGroupActivity;
import com.example.iaeste.general.GroupsFragment.OnListFragmentInteractionListener;
import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.R;
import com.example.iaeste.general.Model.MyGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MyGroup} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyGroupsRecyclerViewAdapter extends RecyclerView.Adapter<MyGroupsRecyclerViewAdapter.ViewHolder> {

    private final List<MyGroup> mValues;
    private final OnListFragmentInteractionListener mListener;

    private Context context;

    public MyGroupsRecyclerViewAdapter(List<MyGroup> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_groups, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getDisplayName());

        DatabaseReference userDabaseReference = FirebaseDatabase.getInstance().getReference("/users/"+mValues.get(position).getOwner_uid());
        userDabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MyUser myUser = dataSnapshot.getValue(MyUser.class);
                holder.mContentView.setText(myUser.getDisplayName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.mDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mGroupDatabaseReference = FirebaseDatabase.getInstance().getReference("groups");
                mGroupDatabaseReference.child((String) mValues.get(position).getId()).removeValue();

                mValues.remove(position);
                notifyItemRemoved(position);
                //this line below gives you the animation and also updates the
                //list items after the deleted item
                notifyItemRangeChanged(position, getItemCount());
            }
        });

        holder.mEditGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditGroupActivity.class);
                intent.putExtra("myGroup", mValues.get(position));
                intent.putExtra("groupName", mValues.get(position).getDisplayName());
                intent.putParcelableArrayListExtra("groupUserList", (ArrayList<? extends Parcelable>) mValues.get(position).getMembers());
                context.startActivity(intent);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageButton mDeleteGroup;
        public final ImageButton mEditGroup;
        public MyGroup mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mDeleteGroup = (ImageButton) view.findViewById(R.id.delete_group);
            mEditGroup = (ImageButton) view.findViewById(R.id.edit_group);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void addValue(MyGroup myGroup){
        mValues.add(myGroup);
    }
}
