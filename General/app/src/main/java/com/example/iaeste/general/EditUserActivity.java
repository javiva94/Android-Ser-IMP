package com.example.iaeste.general;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ListView;

import com.example.iaeste.general.Model.MyGroup;
import com.example.iaeste.general.View.ListSelectionViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EditUserActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mGroupDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

    private ViewStub stubList;
    private ListView listView;
    private ListSelectionViewAdapter listSelectionViewAdapter;

    private List<MyGroup> userGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Intent intent = getIntent();
        userGroupList = intent.getParcelableArrayListExtra("userGroupList");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mGroupDatabaseReference = mFirebaseDatabase.getReference("groups");
        mUserDatabaseReference = mFirebaseDatabase.getReference("users");

        databaseGroupsListInitialization();

        groupsViewListInitialization();
    }

    private void databaseGroupsListInitialization(){
        mGroupDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    MyGroup newMyGroup = markerChild.getValue(MyGroup.class);
                    newMyGroup.setId(markerChild.getKey());
                    listSelectionViewAdapter.add(newMyGroup);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void groupsViewListInitialization(){
        stubList = (ViewStub) findViewById(R.id.stub_list_groups);
        stubList.inflate();

        listView = (ListView) findViewById(R.id.listview_divider);

        listSelectionViewAdapter = new ListSelectionViewAdapter<MyGroup>(this, R.layout.activity_edit_user, userGroupList);
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
