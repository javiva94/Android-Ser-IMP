package com.example.iaeste.general;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.iaeste.general.Model.MapObject;
import com.example.iaeste.general.Model.MyMarker;
import com.example.iaeste.general.Model.MyPolygon;
import com.example.iaeste.general.Model.MyPolyline;
import com.example.iaeste.general.Model.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditMapObjectActivity extends AppCompatActivity {

    private Task task;
    private MapObject mapObject;

    private EditText title;
    private EditText description;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMapObjectDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_map_object);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.DescriptionTitle);

        Intent intent = getIntent(); // gets the previously created intent
        task = (Task) intent.getParcelableExtra("task");
        mapObject = (MapObject) intent.getParcelableExtra("mapObject");



            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(mapObject instanceof MyMarker){
            showPointEdition((MyMarker) mapObject);
        }else{
            if(mapObject instanceof MyPolyline) {
                showPolylineEdition((MyPolyline) mapObject);
            }else{
                if(mapObject instanceof MyPolygon){
                    showPolygonEdition((MyPolygon) mapObject);
                }
            }
        }

    }


    private void showPointEdition(MyMarker myMarker){
        TextView latitude = (TextView) findViewById(R.id.latitudeTextView);
        TextView longitude = (TextView) findViewById(R.id.longitudeTextView);
        latitude.setText(getResources().getString(R.string.Lat)+" "+String.valueOf(myMarker.getPosition().getLatitude()));
        longitude.setText(getResources().getString(R.string.Long)+" "+String.valueOf(myMarker.getPosition().getLongitude()));

        TextView author = (TextView) findViewById(R.id.authorTextView);
        author.setText(getResources().getString(R.string.Author)+" "+ myMarker.getAuthor());

        EditText title = (EditText) findViewById(R.id.title);
        EditText description = (EditText) findViewById(R.id.DescriptionTitle);

        if(myMarker.getTitle()!=null) {
            title.setText(myMarker.getTitle());
        }
        if(myMarker.getDescription()!=null) {
            description.setText(myMarker.getDescription());
        }
    }

    private void showPolylineEdition(MyPolyline polyline){
        TextView author = (TextView) findViewById(R.id.authorTextView);
        author.setText(getResources().getString(R.string.Author)+" "+polyline.getAuthor());

        if(polyline.getTitle()!=null) {
            title.setText(polyline.getTitle());
        }
        if(polyline.getDescription()!=null) {
            description.setText(polyline.getDescription());
        }
    }

    private void showPolygonEdition(MyPolygon polygon){
        TextView author = (TextView) findViewById(R.id.authorTextView);
        author.setText(getResources().getString(R.string.Author)+" "+polygon.getAuthor());

        if(polygon.getTitle()!=null) {
            title.setText(polygon.getTitle());
        }
        if(polygon.getDescription()!=null) {
            description.setText(polygon.getDescription());
        }
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.add(Menu.NONE, 1000, Menu.NONE, R.string.tlb2);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1000:
                updateFirebase();
                finish();
                break;
            case android.R.id.home: //make case to put something here in the future
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void updateFirebase(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMapObjectDatabaseReference = mFirebaseDatabase.getReference("/task/"+task.getKey()+"/mapObjects/");
        Map<String, Object> childUpdates = new HashMap<>();

        if(mapObject instanceof MyMarker){
            MyMarker myMarkerModified = getPointModified((MyMarker) mapObject);
            Map<String, Object> pointValues = myMarkerModified.toMap();
            childUpdates.put(myMarkerModified.getId()+"/MyMarker/", pointValues);
        }else{
            if(mapObject instanceof MyPolyline) {
                MyPolyline polylineModified = getPolylineModified((MyPolyline) mapObject);
                Map<String, Object> polylineValues = polylineModified.toMap();
                childUpdates.put(polylineModified.getId()+"/Polyline/", polylineValues);
            }else{
                if(mapObject instanceof MyPolygon){
                    MyPolygon polygonModified = getPolygonModified((MyPolygon) mapObject);
                    Map<String, Object> polygonValues = polygonModified.toMap();
                    childUpdates.put(polygonModified.getId()+"/Polygon/", polygonValues);
                }
            }
        }

        mMapObjectDatabaseReference.updateChildren(childUpdates);

    }

    private MyMarker getPointModified(MyMarker myMarker){
        EditText title = (EditText) findViewById(R.id.title);
        EditText description = (EditText) findViewById(R.id.DescriptionTitle);

        myMarker.setTitle(title.getText().toString());
        myMarker.setDescription(description.getText().toString());

        return myMarker;
    }

    private MyPolyline getPolylineModified(MyPolyline polyline){
        EditText title = (EditText) findViewById(R.id.title);
        EditText description = (EditText) findViewById(R.id.DescriptionTitle);

        polyline.setTitle(title.getText().toString());
        polyline.setDescription(description.getText().toString());

        return  polyline;
    }

    private MyPolygon getPolygonModified(MyPolygon polygon){
        EditText title = (EditText) findViewById(R.id.title);
        EditText description = (EditText) findViewById(R.id.DescriptionTitle);

        polygon.setTitle(title.getText().toString());
        polygon.setDescription(description.getText().toString());

        return  polygon;
    }
}
