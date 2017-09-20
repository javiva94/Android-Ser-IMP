package com.example.iaeste.general;

import android.content.Intent;
import android.support.constraint.Group;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ListView;

import com.example.iaeste.general.Model.MyGroup;
import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.View.ListSelectionViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mGroupDatabaseReference;
    private DatabaseReference mUserDatabaseReference;

    private ViewStub stubList;
    private ListView listView;
    private ListSelectionViewAdapter listSelectionViewAdapter;

    private List<MyGroup> userGroupList = new ArrayList<>();
    private MyUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Intent intent = getIntent();
        myUser = intent.getParcelableExtra("myUser");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mGroupDatabaseReference = mFirebaseDatabase.getReference("groups");
        mUserDatabaseReference = mFirebaseDatabase.getReference("users");

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

                    //Add to userGroupList those groups where actual user is already in
                    if(newMyGroup.getMembers()!=null) {
                        for(MyUser myUserAux: newMyGroup.getMembers()){
                            if (myUserAux.getUid().equals(myUser.getUid())) {
                                userGroupList.add(newMyGroup);
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

    private void groupsViewListInitialization(){
        stubList = (ViewStub) findViewById(R.id.stub_list_groups);
        stubList.inflate();

        listView = (ListView) findViewById(R.id.listview_divider);

        listSelectionViewAdapter = new ListSelectionViewAdapter<MyGroup>(this, R.layout.activity_edit_user, userGroupList);
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
        final MenuItem createUserMenuItem = menu.add(Menu.NONE, 1000, Menu.NONE, R.string.tlb2);
        MenuItemCompat.setShowAsAction(createUserMenuItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);
        createUserMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                updateDatabase();
                finish();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void updateDatabase(){
        for(int i=0; i<listSelectionViewAdapter.getCount(); i++){
            MyGroup myGroup = (MyGroup) listSelectionViewAdapter.getItem(i);
            List<MyUser> myUserList;
            if(myGroup.getMembers()!=null){
                myUserList = myGroup.getMembers();
            }else{
                myUserList = new ArrayList<>();
            }
            if(containsGroup(listSelectionViewAdapter.getItemsSelected(), myGroup)){
                if(!containsUser(myUserList, myUser)) {
                    myUserList.add(myUser);
                }
            }else{
                if(containsUser(myUserList, myUser)){
                    removeUser(myUserList, myUser);
                }
            }
            mGroupDatabaseReference.child(myGroup.getId()).child("members").setValue(myUserList);
        }

    }

    private boolean containsGroup(List<MyGroup> list, MyGroup element){
        boolean find = false; int i=0;
        while (!find && i<list.size()){
            if(list.get(i).getId().equals(element.getId())){
                find=true;
            }
            i++;
        }
        return find;
    }

    private boolean containsUser(List<MyUser> list, MyUser element){
        boolean find = false; int i=0;
        while (!find && i<list.size()){
            if(list.get(i).getUid().equals(element.getUid())){
                find=true;
            }
            i++;
        }
        return find;
    }

    private void removeUser(List<MyUser> list, MyUser element){
        boolean find = false; int i=0;
        while (!find && i<list.size()){
            if(list.get(i).getUid().equals(element.getUid())){
                list.remove(i);
                find=true;
            }
            i++;
        }
    }

}
