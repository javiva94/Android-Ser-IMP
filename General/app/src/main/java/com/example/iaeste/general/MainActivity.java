package com.example.iaeste.general;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewStub stubGrid;
    private ViewStub stubList;
    private ListView listView;
    private GridView gridView;
    private ListViewAdapter listViewAdapter;
    private GridViewAdapter gridViewAdapter;
    private List<product> productList;
    private int currentViewMode;

    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/

        stubGrid = (ViewStub) findViewById(R.id.stub_grid);
        stubList = (ViewStub) findViewById(R.id.stub_list);

        //inflate viewstub before get view
        stubList.inflate();
        stubGrid.inflate();

        listView = (ListView) findViewById(R.id.myListview);
        gridView = (GridView) findViewById(R.id.mygridview);

        //get list of product
        getproductList();

        //get current view mode in share reference
        SharedPreferences sharedPreference = getSharedPreferences("viewMode", MODE_PRIVATE);
        currentViewMode = sharedPreference.getInt("currentViewMode",VIEW_MODE_LISTVIEW);
       //register item click
       listView.setOnItemClickListener(onItemClick);
        gridView.setOnItemClickListener(onItemClick);

        switchView();

    }

    private void switchView() {
        if(VIEW_MODE_LISTVIEW==currentViewMode){
            //display listview
            stubList.setVisibility(View.VISIBLE);
            //Hide gridView
            stubGrid.setVisibility(View.GONE);
        }else{
            //hide listview
            stubList.setVisibility(View.GONE);
            //display gridView
            stubGrid.setVisibility(View.VISIBLE);
        }
        setAdapters();
    }

    private void setAdapters() {
        if (VIEW_MODE_LISTVIEW == currentViewMode){
            listViewAdapter  = new ListViewAdapter(this,R.layout.list_item, productList);
            listView.setAdapter(listViewAdapter);
    }else{
        gridViewAdapter  = new GridViewAdapter(this,R.layout.grid_item, productList);
        gridView.setAdapter(gridViewAdapter);
        }
    }

    public List<product> getproductList() {
        //pseudo code to get product, replace your code to get real product here
        productList = new ArrayList<>();
        productList.add(new product(R.drawable.world,"Title 1","This is description 1"));
        productList.add(new product(R.drawable.world,"Title 2","This is description 2"));
        productList.add(new product(R.drawable.world,"Title 3","This is description 3"));
        productList.add(new product(R.drawable.world,"Title 4","This is description 4"));
        productList.add(new product(R.drawable.world,"Title 5","This is description 5"));
        productList.add(new product(R.drawable.world,"Title 6","This is description 6"));
        productList.add(new product(R.drawable.world,"Title 7","This is description 7"));
        productList.add(new product(R.drawable.world,"Title 8","This is description 8"));
        productList.add(new product(R.drawable.world,"Title 9","This is description 9"));
        productList.add(new product(R.drawable.world,"Title 10","This is description 10"));

        return productList;
    }
    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // do any thing hen user click to item
            Toast.makeText(getApplicationContext(),productList.get(position).getTitle()+" pendejo " + productList.get(position).getDescription(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_menu_1:
                if (VIEW_MODE_LISTVIEW == currentViewMode){
                    currentViewMode = VIEW_MODE_GRIDVIEW;
                }else{
                    currentViewMode = VIEW_MODE_LISTVIEW;
                }
                //switch view
                switchView();
                //save view mode in share reference
                SharedPreferences sharedPreferences = getSharedPreferences("viewMode", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("currentViewMode", currentViewMode);
                editor.commit();

                break;
        }
        return true;
    }
}
