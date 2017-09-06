package com.example.iaeste.general;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

import com.example.iaeste.general.Model.MyGroup;
import com.example.iaeste.general.Model.MyUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private ArrayList<MyUser> myUserList = new ArrayList<>();
    private ArrayList<MyGroup> myGroupList = new ArrayList<>();

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mGroupDatabaseReference;

    private UsersFragment usersFragment;
    private GroupsFragment groupsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFirebaseDatabase.getReference("users");
        mGroupDatabaseReference = mFirebaseDatabase.getReference("groups");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseUsersListInitialization();

        databaseGroupListInitialization();

        tabHostInitialization();
    }

    private void databaseUsersListInitialization(){
        mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("User element", markerChild.toString());
                    MyUser newMyUser = markerChild.getValue(MyUser.class);
                    if(!myUserList.contains(newMyUser)) {
                        myUserList.add(newMyUser);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void databaseGroupListInitialization(){
        mGroupDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("Group element", markerChild.toString());
                    MyGroup newMyGroup = markerChild.getValue(MyGroup.class);
                    if(!myGroupList.contains(newMyGroup)) {
                        myGroupList.add(newMyGroup);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //make case to put something here in the future
                finish();
                return true;
            case R.id.add_group:
                Intent intent = new Intent(AdminActivity.this, AddGroupActivity.class);
                intent.putParcelableArrayListExtra("usersList", myUserList);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }



    private void tabHostInitialization(){
        TabHost tabHost = (TabHost) findViewById(R.id.tab_host);
        tabHost.setup();

        TabHost.TabSpec tabGroups = tabHost.newTabSpec("Groups");
        TabHost.TabSpec tabUsers = tabHost.newTabSpec("Users");

        tabGroups.setIndicator("Groups");
        tabGroups.setContent(R.id.groups);

        tabUsers.setIndicator("Users");
        tabUsers.setContent(R.id.users);

        tabHost.addTab(tabGroups);
        tabHost.addTab(tabUsers);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("Groups")){
                    groupsFragment = new GroupsFragment();

                    Bundle args = new Bundle();
                    args.putParcelableArrayList("groupsList", myGroupList);
                    groupsFragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.groups, groupsFragment)
                            .commit();
                }
                if(tabId.equals("Users")){
                    usersFragment = new UsersFragment();

                    Bundle args = new Bundle();
                    args.putParcelableArrayList("usersList", myUserList);
                    usersFragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.users, usersFragment)
                            .commit();
                }
            }
        });

    }
}
