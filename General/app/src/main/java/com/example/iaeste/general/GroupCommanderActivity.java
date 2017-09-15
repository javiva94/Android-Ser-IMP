package com.example.iaeste.general;

/**
 * Created by iaeste on 04/09/2017.
 */

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TabHost;

import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.View.UsersPermissionsViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GroupCommanderActivity extends AppCompatActivity {

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
        inflater.inflate(R.menu.groupcommander_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //make case to put something here in the future
                finish();
                return true;
            case R.id.add_user:
                Intent intent1 = new Intent(GroupCommanderActivity.this, AddUserActivity.class);
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

        TabHost.TabSpec tabUsers = tabHost.newTabSpec("Users");

        tabUsers.setIndicator("Users");
        tabUsers.setContent(R.id.users);

        tabHost.addTab(tabUsers);

        tabHost.setCurrentTab(0);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.users, new UsersFragment())
                .commit();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("Users")){
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.users, new UsersFragment())
                            .commit();
                }
            }
        });

    }

}
