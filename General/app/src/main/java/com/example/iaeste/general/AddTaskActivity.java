package com.example.iaeste.general;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ListView;

import com.example.iaeste.general.Model.MyGroup;
import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.Model.Task;
import com.example.iaeste.general.View.ListSelectionViewAdapter;
import com.example.iaeste.general.View.ListViewAdapter;
import com.example.iaeste.general.View.UsersPermissionsViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mTaskDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mGroupDatabaseReference;

    private List<MyUser> myUserList = new ArrayList<>();

    private ViewStub stubList;
    private ListView listView;
    private UsersPermissionsViewAdapter usersPermissionsViewAdapter;
    private ListSelectionViewAdapter<MyGroup> groupListViewAdapter;

    private String actualUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Intent intent = getIntent(); // gets the previously created intent
        actualUserRole = intent.getStringExtra("userRole");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mTaskDatabaseReference = mFirebaseDatabase.getReference("task");
        mUserDatabaseReference = mFirebaseDatabase.getReference("users");
        mGroupDatabaseReference = mFirebaseDatabase.getReference("groups");

        if(actualUserRole.equals("admin")){
            databaseGroupsListInitialization();
            groupsViewListInitialization();
        }else {
            if (actualUserRole.equals("group_commander")) {
                databaseGroupsListInitialization();
                groupsViewListInitialization();
            }
        }

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

    public void addTask(View view){
        EditText taskTitle = (EditText) findViewById(R.id.taskTitle);
        String key = mTaskDatabaseReference.push().getKey();

        Task newTask = new Task();
        newTask.setTitle(taskTitle.getText().toString());
        newTask.setOwner_uid(mFirebaseAuth.getCurrentUser().getUid());

        mTaskDatabaseReference.child(key).setValue(newTask);
        newTask.setKey(key);

        if(actualUserRole.equals("group_commander") || actualUserRole.equals("admin")){
            List<MyGroup> groupsSelected = groupListViewAdapter.getItemsSelected();
            for(MyGroup myGroup : groupsSelected){
                myGroup.getTasks().add(newTask);
                Map<String, Object> childUpdates = new HashMap<>();
                Map<String, Object> myGroupValues = myGroup.toMap();
                childUpdates.put(myGroup.getId(), myGroupValues);
                mGroupDatabaseReference.updateChildren(childUpdates);
            }
        }/*else{
            if(usersPermissionsViewAdapter!=null) {
                for (int i = 0; i < usersPermissionsViewAdapter.getCount(); i++) {
                    String user_uid = usersPermissionsViewAdapter.getItem(i).getUid();
                    if (usersPermissionsViewAdapter.getReadPermissionCheck()[i]) {
                        newTask.getReadUsersPermission().add(user_uid);
                    }
                    if ((usersPermissionsViewAdapter.getWritePermissionCheck()[i])) {
                        newTask.getWriteUsersPermission().add(user_uid);
                    }
                }
            }
        }*/

        Intent intent = new Intent(AddTaskActivity.this , MapsActivity.class);
        intent.putExtra("task", newTask);
        startActivity(intent);
        finish();
    }

    private void databaseGroupsListInitialization(){
        mGroupDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("New element", markerChild.toString());
                    MyGroup newMyGroup = markerChild.getValue(MyGroup.class);
                    newMyGroup.setId(markerChild.getKey());
                    groupListViewAdapter.add(newMyGroup);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void groupsViewListInitialization(){
        stubList = (ViewStub) findViewById(R.id.stub_List_users);
        stubList.inflate();

        listView = (ListView) findViewById(R.id.listview_divider);

        groupListViewAdapter = new ListSelectionViewAdapter<MyGroup>(this, R.layout.activity_add_task);
        listView.setAdapter(groupListViewAdapter);
    }
/*
    private void databaseUsersListInitialization(){
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("New element", markerChild.toString());
                    MyUser newMyUser = markerChild.getValue(MyUser.class);
                    usersPermissionsViewAdapter.add(newMyUser);
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

        usersPermissionsViewAdapter = new UsersPermissionsViewAdapter(this, R.layout.activity_add_task);
        listView.setAdapter(usersPermissionsViewAdapter);
    }
*/


}
