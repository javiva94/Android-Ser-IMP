package com.example.iaeste.general.View;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iaeste.general.AdminActivity;
import com.example.iaeste.general.MainActivity;
import com.example.iaeste.general.R;
import com.example.iaeste.general.UsersFragment;
import com.example.iaeste.general.UsersFragment.OnListFragmentInteractionListener;
import com.example.iaeste.general.Model.MyUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link MyUser} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyUsersRecyclerViewAdapter extends RecyclerView.Adapter<MyUsersRecyclerViewAdapter.ViewHolder> {

    private final List<MyUser> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyUsersRecyclerViewAdapter(List<MyUser> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getDisplayName());
        holder.mContentView.setText(mValues.get(position).getRole());

        holder.mDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference mUserDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
                mUserDatabaseReference.child((String) mValues.get(position).getUid()).removeValue();
//ELIMINAR USUARIO DE AUTH!!!!
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
        public final Button mDeleteUser;
        public MyUser mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.content);
            mContentView = (TextView) view.findViewById(R.id.role);
            mDeleteUser = (Button) view.findViewById(R.id.delete_user);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


    public void addValue(MyUser myUser){
        mValues.add(myUser);
    }

}
