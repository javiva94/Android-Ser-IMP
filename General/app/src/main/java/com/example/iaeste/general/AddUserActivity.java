package com.example.iaeste.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iaeste.general.Model.MyUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/**
 * Created by iaeste on 05/09/2017.
 */

public class AddUserActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();

        userName = (EditText) findViewById(R.id.user_name);
        userEmail = (EditText) findViewById(R.id.user_email);
        userPassword = (EditText) findViewById(R.id.user_password);

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

}
