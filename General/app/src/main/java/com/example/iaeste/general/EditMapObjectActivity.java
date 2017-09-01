package com.example.iaeste.general;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iaeste.general.Model.MapObject;
import com.example.iaeste.general.Model.MyPolygon;
import com.example.iaeste.general.Model.MyPolyline;
import com.example.iaeste.general.Model.Point;
import com.example.iaeste.general.Model.Task;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
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
        description = (EditText) findViewById(R.id.description);

        Intent intent = getIntent(); // gets the previously created intent
        task = (Task) intent.getParcelableExtra("task");
        mapObject = (MapObject) intent.getParcelableExtra("mapObject");



            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        if(mapObject instanceof Point){
            showPointEdition((Point) mapObject);
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

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                    Log.i("ActionBar", "Atrás!");
                    finish();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

    private void showPointEdition(Point point){
        TextView latitude = (TextView) findViewById(R.id.latitudeTextView);
        TextView longitude = (TextView) findViewById(R.id.longitudeTextView);
        latitude.setText("Latitude: "+String.valueOf(point.getPosition().getLatitude()));
        longitude.setText("Longitude: "+String.valueOf(point.getPosition().getLongitude()));

        TextView author = (TextView) findViewById(R.id.authorTextView);
        author.setText("Author: "+point.getAuthor());

        EditText title = (EditText) findViewById(R.id.title);
        EditText description = (EditText) findViewById(R.id.description);

        if(point.getTitle()!=null) {
            title.setText(point.getTitle());
        }
        if(point.getDescription()!=null) {
            description.setText(point.getDescription());
        }
    }

    private void showPolylineEdition(MyPolyline polyline){
        TextView author = (TextView) findViewById(R.id.authorTextView);
        author.setText("Author: "+polyline.getAuthor());

        if(polyline.getTitle()!=null) {
            title.setText(polyline.getTitle());
        }
        if(polyline.getDescription()!=null) {
            description.setText(polyline.getDescription());
        }
    }

    private void showPolygonEdition(MyPolygon polygon){
        TextView author = (TextView) findViewById(R.id.authorTextView);
        author.setText("Author: "+polygon.getAuthor());

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
        final MenuItem menuItem = menu.add(Menu.NONE, 1000, Menu.NONE, "Done");
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }


    private void updateFirebase(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMapObjectDatabaseReference = mFirebaseDatabase.getReference("/task/"+task.getKey()+"/mapObjects/");
        Map<String, Object> childUpdates = new HashMap<>();

        if(mapObject instanceof Point){
            Point pointModified = getPointModified((Point) mapObject);
            Map<String, Object> pointValues = pointModified.toMap();
            childUpdates.put(pointModified.getId()+"/Point/", pointValues);
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

    private Point getPointModified(Point point){
        EditText title = (EditText) findViewById(R.id.title);
        EditText description = (EditText) findViewById(R.id.description);

        point.setTitle(title.getText().toString());
        point.setDescription(description.getText().toString());

        return  point;
    }

    private MyPolyline getPolylineModified(MyPolyline polyline){
        EditText title = (EditText) findViewById(R.id.title);
        EditText description = (EditText) findViewById(R.id.description);

        polyline.setTitle(title.getText().toString());
        polyline.setDescription(description.getText().toString());

        return  polyline;
    }

    private MyPolygon getPolygonModified(MyPolygon polygon){
        EditText title = (EditText) findViewById(R.id.title);
        EditText description = (EditText) findViewById(R.id.description);

        polygon.setTitle(title.getText().toString());
        polygon.setDescription(description.getText().toString());

        return  polygon;
    }
}
