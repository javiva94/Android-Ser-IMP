package com.example.iaeste.general;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.util.Map;

public class EditMapObjectActivity extends AppCompatActivity {

    private Task task;
    private MapObject mapObject;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMapObjectDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_map_object);


        Intent intent = getIntent(); // gets the previously created intent
        task = (Task) intent.getParcelableExtra("task");
        mapObject = (MapObject) intent.getParcelableExtra("mapObject");

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

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    private void showPointEdition(Point point){
        TextView latitude = (TextView) findViewById(R.id.latitudeTextView);
        TextView longitude = (TextView) findViewById(R.id.longitudeTextView);

        latitude.setText("Latitude: "+String.valueOf(point.getPosition().getLatitude()));
        longitude.setText("Longitude: "+String.valueOf(point.getPosition().getLongitude()));
    }

    private void showPolylineEdition(MyPolyline polyline){
        TextView id = (TextView) findViewById(R.id.latitudeTextView);
        id.setText("id:  "+String.valueOf(polyline.getId()));
    }

    private void showPolygonEdition(MyPolygon polygon){
        TextView id = (TextView) findViewById(R.id.latitudeTextView);
        id.setText("id:  "+String.valueOf(polygon.getId()));
    }
}
