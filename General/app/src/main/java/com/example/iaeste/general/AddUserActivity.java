package com.example.iaeste.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.iaeste.general.Model.MyGroup;
import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.View.ListSelectionViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iaeste on 05/09/2017.
 */

public class AddUserActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mGroupDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;

    private ViewStub stubList;
    private ListView listView;
    private ListSelectionViewAdapter listSelectionViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mGroupDatabaseReference = mFirebaseDatabase.getReference("groups");
        mUserDatabaseReference = mFirebaseDatabase.getReference("users");

        userName = (EditText) findViewById(R.id.content);
        userEmail = (EditText) findViewById(R.id.user_email);
        userPassword = (EditText) findViewById(R.id.user_password);

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
        stubList = (ViewStub) findViewById(R.id.stub_list);
        stubList.inflate();

        listView = (ListView) findViewById(R.id.listview_divider);

        listSelectionViewAdapter = new ListSelectionViewAdapter<MyGroup>(this, R.layout.activity_add_user);
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
        final MenuItem createUserMenuItem = menu.add(Menu.NONE, 1000, Menu.NONE, R.string.tlb1);
        MenuItemCompat.setShowAsAction(createUserMenuItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);
        createUserMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addUser();
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void addUser(){
       mFirebaseAuth.createUserWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString())
               .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                   @Override
                   public void onSuccess(AuthResult authResult) {
                       Toast.makeText(AddUserActivity.this, R.string.toast1, Toast.LENGTH_SHORT).show();
                       FirebaseUser user = mFirebaseAuth.getCurrentUser();
                       if (user != null) {
                           UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                   .setDisplayName(userName.getText().toString()).build();
                           user.updateProfile(profileUpdates);
                       }
                       addUserToDatabase();
                       Intent intent = new Intent(AddUserActivity.this, MainActivity.class);
                       startActivity(intent);
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(AddUserActivity.this, R.string.toast2, Toast.LENGTH_SHORT).show();
                   }
               });
    }

    private void addUserToDatabase(){
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        MyUser myUser = new MyUser();
        myUser.setUid(mFirebaseUser.getUid());
        myUser.setDisplayName(userName.getText().toString());
        myUser.setEmail(userEmail.getText().toString());
        myUser.setProviderId(mFirebaseUser.getProviderId());
        myUser.setRole("user");
        mUserDatabaseReference.child(mFirebaseUser.getUid()).setValue(myUser);

        for(MyGroup myGroup : (List<MyGroup>)listSelectionViewAdapter.getItemsSelected()){
            myGroup.getMembers().add(myUser);
            Map<String, Object> childUpdates = new HashMap<>();
            Map<String, Object> myGroupValues = myGroup.toMap();
            childUpdates.put(myGroup.getId(), myGroupValues);
            mGroupDatabaseReference.updateChildren(childUpdates);
        }

    }
}
