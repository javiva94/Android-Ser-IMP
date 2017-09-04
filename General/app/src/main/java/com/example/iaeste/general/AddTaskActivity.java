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

import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.Model.Task;
import com.example.iaeste.general.View.UsersViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTaskActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mTaskDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

    private List<MyUser> myUserList = new ArrayList<>();

    private ViewStub stubList;
    private ListView listView;
    private UsersViewAdapter usersViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mTaskDatabaseReference = mFirebaseDatabase.getReference("task");
        mUserDatabaseReference = mFirebaseDatabase.getReference("users");

        databaseUsersListInitialization();

        usersViewListInitialization();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                Log.i("ActionBar", "Atrás!");
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

        for(int i=0; i< usersViewAdapter.getCount(); i++){
            String user_uid= usersViewAdapter.getItem(i).getUid();
            if(usersViewAdapter.getReadPermissionCheck()[i]){
                newTask.getReadUsersPermission().add(user_uid);
            }
            if((usersViewAdapter.getWritePermissionCheck()[i])){
                newTask.getWriteUsersPermission().add(user_uid);
            }
        }
        mTaskDatabaseReference.child(key).setValue(newTask);
        newTask.setKey(key);


        Intent intent = new Intent(AddTaskActivity.this , MapsActivity.class);
        intent.putExtra("task", newTask);
        startActivity(intent);
        finish();
    }

    private void databaseUsersListInitialization(){
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("New element", markerChild.toString());
                    MyUser newMyUser = markerChild.getValue(MyUser.class);
                    usersViewAdapter.add(newMyUser);

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

        usersViewAdapter = new UsersViewAdapter(this, R.layout.activity_add_task);
        listView.setAdapter(usersViewAdapter);
    }

}
