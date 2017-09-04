package com.example.iaeste.general;

/**
 * Created by iaeste on 04/09/2017.
 */

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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

import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.Model.Point;
import com.example.iaeste.general.Model.Task;
import com.example.iaeste.general.View.UsersViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupCommanderActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUserDatabaseReference;

    private ViewStub stubList;
    private ListView listView;
    private UsersViewAdapter usersViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_task);

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
