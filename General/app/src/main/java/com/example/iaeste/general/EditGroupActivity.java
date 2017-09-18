package com.example.iaeste.general;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ListView;

import com.example.iaeste.general.Model.MyGroup;
import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.View.ListSelectionViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditGroupActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mGroupDatabaseReference;

    private ViewStub stubList;
    private ListView listView;
    private ListSelectionViewAdapter listSelectionViewAdapter;

    private List<MyUser> groupUserList;

    private MyGroup myGroup;
    private EditText group_title_editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        Intent intent = getIntent();
        myGroup = intent.getParcelableExtra("myGroup");
        String groupName = intent.getStringExtra("groupName");
        groupUserList = intent.getParcelableArrayListExtra("groupUserList");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mGroupDatabaseReference = mFirebaseDatabase.getReference("groups");
        mUserDatabaseReference = mFirebaseDatabase.getReference("users");

        databaseUsersListInitialization();

        usersViewListInitialization();

        group_title_editText = (EditText) findViewById(R.id.group_title);
        group_title_editText.setText(groupName);
    }

    private void databaseUsersListInitialization(){
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    MyUser newMyUser = markerChild.getValue(MyUser.class);
                    newMyUser.setUid(markerChild.getKey());
                    listSelectionViewAdapter.add(newMyUser);
                    if(groupUserList!=null && groupUserList.contains(newMyUser)){
                        listSelectionViewAdapter.setItemsSelected(listSelectionViewAdapter.getPosition(newMyUser), newMyUser);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void usersViewListInitialization(){
        stubList = (ViewStub) findViewById(R.id.stub_list_users);
        stubList.inflate();

        listView = (ListView) findViewById(R.id.listview_divider);

        listSelectionViewAdapter = new ListSelectionViewAdapter<MyUser>(this, R.layout.activity_edit_group, groupUserList);
        listView.setAdapter(listSelectionViewAdapter);

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
        final MenuItem createUserMenuItem = menu.add(Menu.NONE, 1000, Menu.NONE, "DONE");
        MenuItemCompat.setShowAsAction(createUserMenuItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);
        createUserMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                updateDatabase();
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void updateDatabase(){
        myGroup.setDisplayName(group_title_editText.getText().toString());
        myGroup.setMembers(listSelectionViewAdapter.getItemsSelected());
        mGroupDatabaseReference.child(myGroup.getId()).setValue(myGroup);
    }

}
