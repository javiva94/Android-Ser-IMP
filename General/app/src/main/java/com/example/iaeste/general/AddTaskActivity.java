package com.example.iaeste.general;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iaeste.general.Model.Point;
import com.example.iaeste.general.Model.Task;
import com.example.iaeste.general.Model.User;
import com.example.iaeste.general.View.UsersViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddTaskActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mTaskDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

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

        showUsersList();

        usersVieListInitialization();

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
        mTaskDatabaseReference.child(key).setValue(newTask);
        newTask.setKey(key);
        newTask.setOwner_uid(mFirebaseAuth.getCurrentUser().getUid());

        Intent intent = new Intent(AddTaskActivity.this , MapsActivity.class);
        intent.putExtra("task", newTask);
        startActivity(intent);
        finish();
    }

    private void showUsersList(){
        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("New element", markerChild.toString());
                    User newUser = markerChild.getValue(User.class);
                    usersViewAdapter.add(newUser);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void usersVieListInitialization(){
        stubList = (ViewStub) findViewById(R.id.stub_List_users);
        stubList.inflate();

        listView = (ListView) findViewById(R.id.myListview);

        usersViewAdapter = new UsersViewAdapter(this, R.layout.activity_add_task);
        listView.setAdapter(usersViewAdapter);
    }


}
