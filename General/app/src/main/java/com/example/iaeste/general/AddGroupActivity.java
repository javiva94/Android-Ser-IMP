package com.example.iaeste.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ListView;

import com.example.iaeste.general.Model.MyGroup;
import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.View.UsersSelectionViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import android.view.MenuItem;
import android.widget.TextView;


/**
 * Created by iaeste on 05/09/2017.
 */

public class AddGroupActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mGroupDatabaseReference;

    private ViewStub stubList;
    private ListView listView;
    private UsersSelectionViewAdapter usersSelectionViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mUserDatabaseReference = mFirebaseDatabase.getReference("users");

        databaseUsersListInitialization();

        usersViewListInitialization();
    }

    private void databaseUsersListInitialization(){
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("New element", markerChild.toString());
                    MyUser newMyUser = markerChild.getValue(MyUser.class);
                    usersSelectionViewAdapter.add(newMyUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void usersViewListInitialization(){
        stubList = (ViewStub) findViewById(R.id.stub_List_users);
        stubList.inflate();

        listView = (ListView) findViewById(R.id.myListview);

        usersSelectionViewAdapter = new UsersSelectionViewAdapter(this, R.layout.activity_add_task);
        listView.setAdapter(usersSelectionViewAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //make case to put something here in the future
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuItem createGroupMenuItem= menu.add(Menu.NONE, 1000, Menu.NONE, "Create");
        MenuItemCompat.setShowAsAction(createGroupMenuItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);
        createGroupMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addGroup();
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void addGroup(){
        mGroupDatabaseReference = mFirebaseDatabase.getReference("/groups/");
        String key =  mGroupDatabaseReference.push().getKey();
        usersSelectionViewAdapter.getUsersSelected();
        EditText groupTitle = (EditText) findViewById(R.id.group_title);
        MyGroup newGroup = new MyGroup(groupTitle.getText().toString(), usersSelectionViewAdapter.getUsersSelected(), mFirebaseAuth.getCurrentUser().getUid());
        mGroupDatabaseReference.child(key).setValue(newGroup);
    }
}
