package com.example.iaeste.general;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.List;

public class EditGroupActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mGroupDatabaseReference;

    private ViewStub stubList;
    private ListView listView;
    private ListSelectionViewAdapter listSelectionViewAdapter;

    private List<MyUser> groupUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        Intent intent = getIntent();
        String groupName = intent.getStringExtra("groupName");
        groupUserList = intent.getParcelableArrayListExtra("groupUserList");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mUserDatabaseReference = mFirebaseDatabase.getReference("users");

        databaseUsersListInitialization();

        usersViewListInitialization();

        EditText group_title = (EditText) findViewById(R.id.group_title);
        group_title.setText(groupName);
    }

    private void databaseUsersListInitialization(){
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    MyUser newMyUser = markerChild.getValue(MyUser.class);
                    newMyUser.setUid(markerChild.getKey());
                    listSelectionViewAdapter.add(newMyUser);
                    if(groupUserList.contains(newMyUser)){
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

}
