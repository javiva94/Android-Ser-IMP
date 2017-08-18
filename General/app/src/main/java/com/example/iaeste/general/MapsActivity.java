package com.example.iaeste.general;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.iaeste.general.Model.MapObject;
import com.example.iaeste.general.Model.Point;
import com.example.iaeste.general.Model.Task;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class MapsActivity extends AppCompatActivity implements LocationListener {

    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private static final String MYTAG = "MYTAG";
    FloatingActionButton addObj_3,addObj_2,addObj_1,addObj;
    Animation FabOpen,FabClose,FabClockWise,Fabanticlockwise;
    boolean isOpen = false;


    // Request Code to ask the user for permission to view their current location (***).
    // Value 8bit (value <256)
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    private Task task;

    private List<Marker> listMarkers = new ArrayList<>();
    private Marker myPosition;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mTaskDatabaseReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent(); // gets the previously created intent
        task = (Task) intent.getParcelableExtra("task");

        Toast.makeText(this, "id: "+task.getKey(),Toast.LENGTH_LONG).show();

        addObj = (FloatingActionButton) findViewById(R.id.addObj);
        addObj_1 = (FloatingActionButton) findViewById(R.id.addObj_1);
        addObj_2 = (FloatingActionButton) findViewById(R.id.addObj_2);
        addObj_3 = (FloatingActionButton) findViewById(R.id.addObj_3);
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        FabClockWise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        Fabanticlockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        addObj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOpen)
                {
                    addObj_1.startAnimation(FabClose);
                    addObj_2.startAnimation(FabClose);
                    addObj_3.startAnimation(FabClose);
                    addObj.startAnimation(Fabanticlockwise);
                    addObj_1.setClickable(false);
                    addObj_2.setClickable(false);
                    addObj_3.setClickable(false);
                    isOpen=false;
                }
                    else
                {
                    addObj_1.startAnimation(FabOpen);
                    addObj_2.startAnimation(FabOpen);
                    addObj_3.startAnimation(FabOpen);
                    addObj.startAnimation(FabClockWise);
                    addObj_1.setClickable(true);
                    addObj_2.setClickable(true);
                    addObj_3.setClickable(true);
                    isOpen=true;

                }
            }
        });
        // Create Progress Bar.
        myProgress = new ProgressDialog(this);
        myProgress.setTitle("Map Loading ...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);
        // Display Progress Bar.
        myProgress.show();

        MapFragment mapFragment
                = (MapFragment) getFragmentManager().findFragmentById(R.id.mMapView);

        // Set callback listener, on Google Map ready.
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
               // addMapObjects();
            }
        });

        firebaseDatabaseInit();
    }

    private void firebaseDatabaseInit(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mTaskDatabaseReference = mFirebaseDatabase.getReference(task.getKey());

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Point newPoint = dataSnapshot.getValue(Point.class);
                task.getMapObjects().add(newPoint);
                Marker newMarker = myMap.addMarker(new MarkerOptions().position(newPoint.getLatLng()));
                listMarkers.add(newMarker);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Point newPoint = dataSnapshot.getValue(Point.class);
                task.getMapObjects().remove(newPoint);
                Marker markerToRemove = listMarkers.get(0);
                markerToRemove.remove();
                listMarkers.remove(markerToRemove);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mTaskDatabaseReference.addChildEventListener(mChildEventListener);
    }

    private void addMapObjects(){
        for(Iterator<MapObject> i= task.getMapObjects().iterator(); i.hasNext();){
            MapObject mapObject = i.next();
            myMap.addMarker(new MarkerOptions().position(mapObject.getLatLng()));

        }
    }


    private void onMyMapReady(final GoogleMap googleMap) {
        // Get Google Map from Fragment.
        myMap = googleMap;
        // Sét OnMapLoadedCallback Listener.
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                // Map loaded. Dismiss this dialog, removing it from the screen.
                myProgress.dismiss();
                askPermissionsAndShowMyLocation();
            }
        });
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(true);

        addMapObjects();

        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
               // listLatLng.add(point);
                Point newPointObject = new Point(point);
                myMap.addMarker(new MarkerOptions().position(point));
                task.getMapObjects().add(newPointObject);
                updateFirebaseDB();
            }
        });
    }

    private void updateFirebaseDB(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mTaskDatabaseReference = mFirebaseDatabase.getReference();

        Map<String, Object> taskValues = task.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/task/" + task.getKey(), taskValues);

        mTaskDatabaseReference.updateChildren(childUpdates);
    }


    private void askPermissionsAndShowMyLocation() {

        // With API> = 23, you have to ask the user for permission to view their location.
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);


            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
                // The Permissions to ask user.
                String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};
                // Show a dialog asking the user to allow the above permissions.
                ActivityCompat.requestPermissions(this, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);

                return;
            }
        }

        // Show current location on Map.
        this.showMyLocation();
    }

    // When you have the request results.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (read/write).
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                    // Show current location on Map.
                    this.showMyLocation();
                }
                // Cancelled or denied.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    // Find Location provider is openning.
    private String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Criteria to find location provider.
        Criteria criteria = new Criteria();

        // Returns the name of the provider that best meets the given criteria.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(this, "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }

   // Circle circle;
    // Call this method only when you have the permissions to view a user's location.
    private void showMyLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProvider = this.getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        Location myLocation = null;
        try {
            // This code need permissions (Asked above ***)
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
            // Getting Location.
            // Lấy ra vị trí.
            myLocation = locationManager
                    .getLastKnownLocation(locationProvider);
        }
        // With Android API >= 23, need to catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(this, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(MYTAG, "Show My Location Error:" + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (myLocation != null) {

            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            // Add Marker to Map
            MarkerOptions option = new MarkerOptions();
            option.title("My Location");
            option.snippet("....");
            option.position(latLng);
            myPosition = myMap.addMarker(option);
            myPosition.showInfoWindow();

           // circle = drawCircle(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not found!", Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "Location not found");
        }

    }
/*
    private Circle drawCircle(LatLng latLng) {

        CircleOptions options = new CircleOptions()
                .center(latLng)
                .radius(100)
                .fillColor(0x20ff0100)
                .strokeWidth(2);

        return myMap.addCircle(options);
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Menu_typeNone :
                myMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.Menu_typeNormal :
                myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.Menu_typeTerrain :
                myMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.Menu_typeSatellite :
                myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.Menu_typeHybrid:
                myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.bookmark:
                Intent bookmarkAct = new Intent(this, BookMarkActivity.class);
                this.startActivity(bookmarkAct);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void Clear(View view) {
        if(listMarkers!=null) {
            myMap.clear();
            listMarkers = new ArrayList<>();
            showMyLocation();
        }
    }

}