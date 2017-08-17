package com.example.iaeste.general;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iaeste.general.Model.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTaskActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mTaskDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mTaskDatabaseReference = mFirebaseDatabase.getReference("task");
    }

    public void addTask(View view){
        EditText taskTitle = (EditText) findViewById(R.id.taskTitle);
        String key = mTaskDatabaseReference.push().getKey();
        Task newTask = new Task();
        newTask.setTitle(taskTitle.getText().toString());
        mTaskDatabaseReference.child(key).setValue(newTask);
        newTask.setKey(key);
        finish();
    }
}
