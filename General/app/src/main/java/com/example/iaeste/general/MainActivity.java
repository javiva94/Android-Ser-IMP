package com.example.iaeste.general;

import android.content.Intent;

import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.iaeste.general.Model.MyGroup;
import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.Model.Task;
import com.example.iaeste.general.View.GridViewAdapter;
import com.example.iaeste.general.View.ListViewAdapter;
import com.example.iaeste.general.View.TaskViewAdapter;
import com.firebase.ui.auth.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.util.Arrays;

import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private ViewStub stubGrid;
    private ViewStub stubList;
    private ListView listView;
    private GridView gridView;
    private TaskViewAdapter taskViewAdapter;
    private List<Task> taskList;
    private int currentViewMode;

    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;
    public static final int RC_SIGN_IN = 1;
    private String actualUserRole = "user";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mTaskDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mGroupDatabaseReference;
    private ChildEventListener mChildEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskList = new ArrayList<Task>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.tlb3);
        setSupportActionBar(toolbar);

       /* FloatingActionButton floatingMapButton = (FloatingActionButton) findViewById(R.id.showMapBtn);
        floatingMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });*/

        FloatingActionButton floatingAddTaskButton = (FloatingActionButton) findViewById(R.id.addTaskBtn);
        floatingAddTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                intent.putExtra("userRole", actualUserRole);
                startActivity(intent);
            }
        });

        firebaseAuthenticationInit();

        firebaseUsersDatabaseInit();

        firebaseTaskDatabaseInit();

        taskListViewInitialization();
    }

    private void firebaseAuthenticationInit(){
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if(mFirebaseUser != null){
                    // user is signed in
                    mUserDatabaseReference = mFirebaseDatabase.getReference("users");
                    mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            MyUser user = dataSnapshot.child(mFirebaseUser.getUid()).getValue(MyUser.class);
                            if (user == null) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("displayName", mFirebaseUser.getDisplayName());
                                map.put("email", mFirebaseUser.getEmail());
                                map.put("providerId", mFirebaseUser.getProviderId());
                                map.put("role", "user");
                                mUserDatabaseReference.child(mFirebaseUser.getUid()).setValue(map);
                                Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                   // Toast.makeText(MainActivity.this, "You are now signed in", Toast.LENGTH_SHORT).show();
                }else{
                    //user is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void firebaseUsersDatabaseInit(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFirebaseDatabase.getReference("users");

        mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mFirebaseUser != null) {
                    MyUser user = dataSnapshot.child(mFirebaseUser.getUid()).getValue(MyUser.class);
                    if (user != null) {
                        if (user.getRole().equals("admin")) {
                            actualUserRole = "admin";
                            FloatingActionButton floatingAdminActivity = (FloatingActionButton) findViewById(R.id.AdminActivityBtn);
                            floatingAdminActivity.setVisibility(View.VISIBLE);
                            floatingAdminActivity.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(MainActivity.this, AdminActivity.class));
                                }
                            });
                            Toast.makeText(MainActivity.this, R.string.toast4, Toast.LENGTH_LONG).show();
                        } else {
                            if (user.getRole().equals("group_commander")) {
                                actualUserRole = "group_commander";
                                FloatingActionButton floatingGroupCommanderActivity = (FloatingActionButton) findViewById(R.id.GroupCommanderActivityBtn);
                                floatingGroupCommanderActivity.setVisibility(View.VISIBLE);
                                floatingGroupCommanderActivity.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(MainActivity.this, GroupCommanderActivity.class));
                                    }
                                });
                                Toast.makeText(MainActivity.this, R.string.toast5, Toast.LENGTH_LONG).show();
                            } else {
                                if (user.getRole().equals("user")) {
                                    Toast.makeText(MainActivity.this, R.string.toast6, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void firebaseTaskDatabaseInit(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mTaskDatabaseReference = mFirebaseDatabase.getReference("task");
        mGroupDatabaseReference = mFirebaseDatabase.getReference("groups");

        mGroupDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyGroup newMyGroup = dataSnapshot.getValue(MyGroup.class);
                if(newMyGroup.getMembers() != null) {
                    for (MyUser myUser : newMyGroup.getMembers()) {
                        if (myUser.getUid().equals(mFirebaseUser.getUid())) {
                            for (Task task : newMyGroup.getTasks()) {
                                taskViewAdapter.add(task);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/*
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Task newTask = dataSnapshot.getValue(Task.class);
                // newTask.setTitle((String) dataSnapshot.child("title").getValue());
                newTask.setKey(dataSnapshot.getKey());
                if(taskList.contains(newTask)) {
                    taskViewAdapter.add(newTask);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                boolean find = false;
                int i = 0;
                while (!find){
                    if(taskList.get(i).getKey().equals(key)){
                        taskViewAdapter.remove(taskList.get(i));
                        find = true;
                    }
                    i++;
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mTaskDatabaseReference.addChildEventListener(mChildEventListener);*/
    }

    private void taskListViewInitialization(){
        stubGrid = (ViewStub) findViewById(R.id.stub_grid);
        stubList = (ViewStub) findViewById(R.id.stub_list);

        //inflate viewstub before get view
        stubList.inflate();
        stubGrid.inflate();

        listView = (ListView) findViewById(R.id.myListview);
        gridView = (GridView) findViewById(R.id.mygridview);

        //get current view mode in share reference
        SharedPreferences sharedPreference = getSharedPreferences("viewMode", MODE_PRIVATE);
        currentViewMode = sharedPreference.getInt("currentViewMode",VIEW_MODE_LISTVIEW);
        //register item click
        listView.setOnItemClickListener(onItemClick);
        gridView.setOnItemClickListener(onItemClick);

        listView.setOnItemLongClickListener(onItemLongClickListener);
        gridView.setOnItemLongClickListener(onItemLongClickListener);

        switchView();
    }

    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // do any thing hen user click to item
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            Task task = taskViewAdapter.getItem(position);
            intent.putExtra("task", task);
            startActivity(intent);
        }
    };

    AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showPopup(view, position);
            return true;
        }
    };

    public void showPopup(final View view, final int position){
        final PopupMenu popup = new PopupMenu(MainActivity.this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_task_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deleteTask(position);
                Toast.makeText(MainActivity.this, R.string.toast7+position, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void deleteTask(int position){
        Task taskToRemove = taskViewAdapter.getItem(position);
        mTaskDatabaseReference = mFirebaseDatabase.getReference("task");
        mTaskDatabaseReference.child(taskToRemove.getKey()).removeValue();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(requestCode == RESULT_OK){
                Toast.makeText(this, R.string.toast8, Toast.LENGTH_SHORT).show();
            }else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, R.string.toast9, Toast.LENGTH_SHORT).show();
                finish();
                }
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void switchView() {
        if(VIEW_MODE_LISTVIEW==currentViewMode){
            //display listview
            stubList.setVisibility(View.VISIBLE);
            //Hide gridView
            stubGrid.setVisibility(View.GONE);
        }else{
            //hide listview
            stubList.setVisibility(View.GONE);
            //display gridView
            stubGrid.setVisibility(View.VISIBLE);
        }
        setAdapters();
    }

    private void setAdapters() {
        if (VIEW_MODE_LISTVIEW == currentViewMode){
            taskViewAdapter  = new ListViewAdapter(this,R.layout.list_item, taskList);
            listView.setAdapter(taskViewAdapter);
    }else{
        taskViewAdapter  = new GridViewAdapter(this,R.layout.grid_item, taskList);
        gridView.setAdapter(taskViewAdapter);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                //sign out
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.item_menu_1:
                if (VIEW_MODE_LISTVIEW == currentViewMode){
                    currentViewMode = VIEW_MODE_GRIDVIEW;
                }else{
                    currentViewMode = VIEW_MODE_LISTVIEW;
                }
                //switch view
                switchView();
                //save view mode in share reference
                SharedPreferences sharedPreferences = getSharedPreferences("viewMode", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("currentViewMode", currentViewMode);
                editor.commit();

                break;
        }
        return  true;
    }
}
