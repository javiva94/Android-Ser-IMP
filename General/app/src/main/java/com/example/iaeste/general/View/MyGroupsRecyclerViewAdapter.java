package com.example.iaeste.general.View;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.iaeste.general.GroupsFragment.OnListFragmentInteractionListener;
import com.example.iaeste.general.R;
import com.example.iaeste.general.Model.MyGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MyGroup} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyGroupsRecyclerViewAdapter extends RecyclerView.Adapter<MyGroupsRecyclerViewAdapter.ViewHolder> {

    private final List<MyGroup> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyGroupsRecyclerViewAdapter(List<MyGroup> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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
        holder.mIdView.setText(mValues.get(position).getId());
        holder.mContentView.setText(mValues.get(position).getDisplayName());

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
        public final Button mDeleteGroup;
        public MyGroup mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mDeleteGroup = (Button) view.findViewById(R.id.delete_group);
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
