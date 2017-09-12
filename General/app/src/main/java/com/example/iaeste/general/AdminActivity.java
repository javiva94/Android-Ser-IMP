package com.example.iaeste.general;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

import com.example.iaeste.general.Model.MyGroup;
import com.example.iaeste.general.Model.MyUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tabHostInitialization();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //make case to put something here in the future
                finish();
                return true;
            case R.id.add_group:
                Intent intent = new Intent(AdminActivity.this, AddGroupActivity.class);
                startActivity(intent);
                break;
            case R.id.add_user:
                Intent intent1 = new Intent(AdminActivity.this, AddUserActivity.class);
                startActivity(intent1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void tabHostInitialization(){
        TabHost tabHost = (TabHost) findViewById(R.id.tab_host);
        tabHost.setup();

        TabHost.TabSpec tabGroups = tabHost.newTabSpec("Groups");
        TabHost.TabSpec tabUsers = tabHost.newTabSpec("Users");

        tabGroups.setIndicator("Groups");
        tabGroups.setContent(R.id.groups);

        tabUsers.setIndicator("Users");
        tabUsers.setContent(R.id.users);

        tabHost.addTab(tabGroups);
        tabHost.addTab(tabUsers);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("Groups")){
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.groups, new GroupsFragment())
                            .commit();
                }
                if(tabId.equals("Users")){
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.users, new UsersFragment())
                            .commit();
                }
            }
        });

    }

}
