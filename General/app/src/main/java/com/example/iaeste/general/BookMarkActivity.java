package com.example.iaeste.general;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.iaeste.general.R;import java.util.ArrayList;

public class BookMarkActivity extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    private ListView list;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        list = (ListView) findViewById(R.id.ListBookmark);
        mDatabaseHelper = new DatabaseHelper(this);
        inti();

    }
    public void inti(){
        //Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
       // while(data.moveToNext()){
       //     listData.add(data.getString(1));
       // }
        ListAdapter adapter = new ArrayAdapter<>(this, R.layout.each_item,listData);
        list.setAdapter(adapter);
    }

}
