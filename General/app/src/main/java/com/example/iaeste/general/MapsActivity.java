package com.example.iaeste.general;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import com.example.iaeste.general.Model.MyPolyline;
import com.example.iaeste.general.Model.MyLatLng;
import com.example.iaeste.general.Model.MyPolygon;
import com.example.iaeste.general.Model.Point;
import com.example.iaeste.general.Model.Task;
import com.example.iaeste.general.View.MyInfoWindow;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements LocationListener {

    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private static final String MYTAG = "MYTAG";
    FloatingActionButton addObj_3, addObj_2, addObj_1, addObj, trash, edit;
    Animation FabOpen, FabClose, FabClockWise, Fabanticlockwise;
    boolean isOpen = false;


    // Request Code to ask the user for permission to view their current location (***).
    // Value 8bit (value <256)
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    private Task task;
    private List<LatLng> listPointsForPolyline = new ArrayList<>();
    private List<LatLng> listPointsForPolygon = new ArrayList<>();

    private HashMap<String, Marker> markerHashMap = new HashMap<>();
    private HashMap<String, Polyline> polylineHashMap = new HashMap<>();
    private HashMap<String, Polygon> polygonHashMap = new HashMap<>();
    private Polygon auxPolygonToShow;
    private Polyline auxPolylineToShow;

    private Marker myPosition;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mMapObjectsDatabaseReference;
    private ChildEventListener mChildEventListener;

    InfoWindowFragment infoWindowFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent(); // gets the previously created intent
        task = (Task) intent.getParcelableExtra("task");

        Toast.makeText(this, "id: " + task.getKey(), Toast.LENGTH_LONG).show();


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


        setFloatingButtons();

        infoWindowFragment = new InfoWindowFragment();

        // Create Progress Bar.
        myProgress = new ProgressDialog(this);
        myProgress.setTitle(R.string.load);
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
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabaseInit();
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
        myMap.getUiSettings().setZoomControlsEnabled(false);
        myMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myMap.setMyLocationEnabled(true);

        myMap.setInfoWindowAdapter(new MyInfoWindow(getLayoutInflater()));
        myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();
            }
        });

        setInfoWindowFragmentListeners();

    }

    private void deletePointFromFirebase(Marker marker){
        String key = (String) marker.getTag();
        mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/"+task.getKey()+"/mapObjects/");
        mMapObjectsDatabaseReference.child(key).removeValue();
    }

    private void deleteLineFromFirebase(Polyline polyline){
        String key = (String) polyline.getTag();
        mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/"+task.getKey()+"/mapObjects/");
        mMapObjectsDatabaseReference.child(key).removeValue();
    }

    private void deletePolygonFromFirebase(Polygon polygon){
        String key = (String) polygon.getTag();
        mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/"+task.getKey()+"/mapObjects/");
        mMapObjectsDatabaseReference.child(key).removeValue();
    }

    private void firebaseDatabaseInit(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/"+task.getKey()+"/mapObjects/");

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("New element", markerChild.toString());
                    //Punto
                    if (markerChild.getKey().equals("Point")) {
                        Point newPoint = markerChild.getValue(Point.class);
                        addPoint(newPoint);
                    }

                    //Linea
                    if (markerChild.getKey().equals("Polyline")){
                        MyPolyline newMyPolyline = markerChild.getValue(MyPolyline.class);
                        addPolyline(newMyPolyline);
                    }

                    //Poligono
                    if (markerChild.getKey().equals("Polygon")){
                        MyPolygon newPolygon = markerChild.getValue(MyPolygon.class);
                        addPolygon(newPolygon);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("Element modified", markerChild.toString());
                    //Punto
                    if (markerChild.getKey().equals("Point")) {
                        Point pointModified = markerChild.getValue(Point.class);
                        removePoint(pointModified);
                        addPoint(pointModified);
                    }

                    //Linea
                    if (markerChild.getKey().equals("Polyline")){
                        MyPolyline myPolylineModified = markerChild.getValue(MyPolyline.class);
                        removePolyline(myPolylineModified);
                        addPolyline(myPolylineModified);
                    }

                    //Poligono
                    if (markerChild.getKey().equals("Polygon")){
                        MyPolygon myPolygonModified = markerChild.getValue(MyPolygon.class);
                        removePolygon(myPolygonModified);
                        addPolygon(myPolygonModified);
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for(DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("Element to delete", markerChild.toString());

                    //Punto
                    if (markerChild.getKey().equals("Point")) {
                        Point pointToRemove = markerChild.getValue(Point.class);
                        removePoint(pointToRemove);
                        Toast.makeText(MapsActivity.this,R.string.point, Toast.LENGTH_SHORT).show();
                    }

                    //Linea
                    if(markerChild.getKey().equals("Polyline")){
                        MyPolyline myPolylineToRemove = markerChild.getValue(MyPolyline.class);
                        removePolyline(myPolylineToRemove);
                        Toast.makeText(MapsActivity.this, R.string.polyline, Toast.LENGTH_SHORT).show();
                    }

                    //Poligono
                    if (markerChild.getKey().equals("Polygon")){
                        MyPolygon polygonToRemove = markerChild.getValue(MyPolygon.class);
                        removePolygon(polygonToRemove);
                        Toast.makeText(MapsActivity.this,R.string.polygon, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                for(DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("Element moved", markerChild.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DB ERROR message", databaseError.getMessage());
                Log.e("DB ERROR details", databaseError.getDetails());
            }
        };
        mMapObjectsDatabaseReference.addChildEventListener(mChildEventListener);
    }

    private void addPoint(Point point){
        Marker newMarker = myMap.addMarker(
                new MarkerOptions()
                        .position(new LatLng(point.getPosition().getLatitude(),point.getPosition().getLongitude()))
                        .title(point.getTitle())
                        .snippet(point.getAuthor()+"/&"+ point.getDescription())
        );
        newMarker.setTag(point.getId());
        markerHashMap.put(point.getId(), newMarker);
        task.getPointList().add(point);
    }

    private void removePoint(Point point){
        Marker markerToRemove = markerHashMap.get(point.getId());
        markerToRemove.remove();
        markerHashMap.remove(markerToRemove);
        task.getPointList().remove(point);
    }

    private void addPolyline (MyPolyline myPolyline){
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(convertMyLatLngToLatLng(myPolyline.getPoints()));

        Polyline newPolyline = myMap.addPolyline(polylineOptions);
        newPolyline.setClickable(true);
        newPolyline.setTag(myPolyline.getId());

        polylineHashMap.put(myPolyline.getId(), newPolyline);
        task.getPolylineList().add(myPolyline);
    }

    private void removePolyline(MyPolyline myPolyline){
        Polyline polylineToRemove = polylineHashMap.get(myPolyline.getId());
        polylineToRemove.remove();
        polylineHashMap.remove(polylineToRemove);
        task.getPolylineList().remove(polylineToRemove);
    }

    private void addPolygon(MyPolygon myPolygon){
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(convertMyLatLngToLatLng(myPolygon.getVertices()));

        Polygon newPolygon = myMap.addPolygon(polygonOptions);
        newPolygon.setClickable(true);
        newPolygon.setTag(myPolygon.getId());

        polygonHashMap.put(myPolygon.getId(), newPolygon);
        task.getPolygonList().add(myPolygon);
    }

    private void removePolygon(MyPolygon myPolygon){
        Polygon polygonToRemove = polygonHashMap.get(myPolygon.getId());
        polygonToRemove.remove();
        polygonHashMap.remove(polygonToRemove);
        task.getPolygonList().remove(polygonToRemove);
    }


    public void Dot (View view) {
        cancelPreviousIncompleteActions();
        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/"+task.getKey());
                String key = mMapObjectsDatabaseReference.push().getKey();
                Point newPoint = new Point(key,
                        new MyLatLng(point.latitude, point.longitude));
                newPoint.setUid(mFirebaseAuth.getCurrentUser().getUid());
                newPoint.setAuthor(mFirebaseAuth.getCurrentUser().getDisplayName());
                mMapObjectsDatabaseReference.child("mapObjects").child(key).child("Point").setValue(newPoint);
            }

        });
    }

    public void Multiline (View view) {
        cancelPreviousIncompleteActions();
        listPointsForPolyline.clear();
        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                listPointsForPolyline.add(point);
                if(auxPolylineToShow != null) {
                    auxPolylineToShow.remove();
                }
                PolylineOptions polylineOptions= new PolylineOptions();
                polylineOptions.addAll(listPointsForPolyline);
                auxPolylineToShow = myMap.addPolyline(polylineOptions);

                if(listPointsForPolyline.size()==2) {
                    setFinishPolylineButton();
                }
            }
        });
    }

    public void Polygon (View view) {
        cancelPreviousIncompleteActions();
        listPointsForPolygon.clear();
        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                listPointsForPolygon.add(point);
                if(auxPolygonToShow != null) {
                    auxPolygonToShow.remove();
                }
                PolygonOptions polygonOptions= new PolygonOptions();
                polygonOptions.addAll(listPointsForPolygon);
                auxPolygonToShow = myMap.addPolygon(polygonOptions);

                if(listPointsForPolygon.size()==3) {
                    setFinishPolygonButton();
                }
            }
        });
    }

    public void remove (View view){
        myMap.setOnMapClickListener(null);
        setFinishDeleteButton();
        myMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                deletePointFromFirebase(marker);
                return false;
            }
        });

        myMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                deleteLineFromFirebase(polyline);
            }
        });

        myMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                deletePolygonFromFirebase(polygon);
            }
        });
    }

    public void edit (View view){
        myMap.setOnMapClickListener(null);
        edit.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.buttonPressedEdit)}));

        myMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                edit.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorPrimary)}));
                myMap.setOnMarkerClickListener(null);
                setInfoWindowFragmentListeners();
                Intent intent = new Intent(MapsActivity.this, EditMapObjectActivity.class);
                intent.putExtra("task", task);
                intent.putExtra("mapObject", task.getPointById((String) marker.getTag()));
                startActivity(intent);
                return false;
            }
        });

        myMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                edit.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorPrimary)}));
                myMap.setOnPolylineClickListener(null);
                setInfoWindowFragmentListeners();
                Intent intent = new Intent(MapsActivity.this, EditMapObjectActivity.class);
                intent.putExtra("task", task);
                intent.putExtra("mapObject", task.getPolylineById((String) polyline.getTag()));
                startActivity(intent);
            }
        });

        myMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                edit.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorPrimary)}));
                myMap.setOnPolygonClickListener(null);
                setInfoWindowFragmentListeners();
                Intent intent = new Intent(MapsActivity.this, EditMapObjectActivity.class);
                intent.putExtra("task", task);
                intent.putExtra("mapObject", task.getPolygonById((String) polygon.getTag()));
                startActivity(intent);
            }
        });
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

                    Toast.makeText(this,R.string.per1, Toast.LENGTH_LONG).show();

                    // Show current location on Map.
                    this.showMyLocation();
                }
                // Cancelled or denied.
                else {
                    Toast.makeText(this,R.string.per2, Toast.LENGTH_LONG).show();
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
            Toast.makeText(this,R.string.per3, Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, R.string.toast10 + e.getMessage(), Toast.LENGTH_LONG).show();
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
          /*  MarkerOptions option = new MarkerOptions();
            option.title("My Location");
            option.snippet("....");
            option.position(latLng);
            myPosition = myMap.addMarker(option);
            myPosition.setTag("MyLocation");
            myPosition.showInfoWindow();*/

           // circle = drawCircle(new MyLatLng(myLocation.getLatitude(), myLocation.getLongitude()));
        } else {
            Toast.makeText(this, R.string.toast11, Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "Location not found");
        }

    }
/*
    private Circle drawCircle(MyLatLng latLng) {

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
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
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

    private void setFloatingButtons(){
        addObj = (FloatingActionButton) findViewById(R.id.addObj);
        addObj_1 = (FloatingActionButton) findViewById(R.id.addObj_1);
        addObj_2 = (FloatingActionButton) findViewById(R.id.addObj_2);
        addObj_3 = (FloatingActionButton) findViewById(R.id.addObj_3);
        trash = (FloatingActionButton) findViewById(R.id.trash);
        edit = (FloatingActionButton) findViewById(R.id.edit);
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        FabClockWise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);
        Fabanticlockwise = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);
        addObj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isOpen) {
                    addObj_1.startAnimation(FabClose);
                    addObj_2.startAnimation(FabClose);
                    addObj_3.startAnimation(FabClose);
                    addObj.startAnimation(Fabanticlockwise);
                    addObj_1.setClickable(false);
                    addObj_2.setClickable(false);
                    addObj_3.setClickable(false);
                    trash.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                    myMap.setOnMapClickListener(null);
                    setInfoWindowFragmentListeners();
                    isOpen = false;
                } else {
                    addObj_1.startAnimation(FabOpen);
                    addObj_2.startAnimation(FabOpen);
                    addObj_3.startAnimation(FabOpen);
                    addObj.startAnimation(FabClockWise);
                    addObj_1.setClickable(true);
                    addObj_2.setClickable(true);
                    addObj_3.setClickable(true);
                    trash.setVisibility(View.GONE);
                    edit.setVisibility(View.GONE);
                    isOpen = true;
                }
            }
        });
    }

    private void setFinishPolygonButton(){
        final Button finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setText(R.string.txt5);
        finishButton.setVisibility(View.VISIBLE);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPointsForPolygon.size() >= 3) {
                    mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/" + task.getKey());
                    String key = mMapObjectsDatabaseReference.push().getKey();

                    List<MyLatLng> myLatLngList = new ArrayList<>();
                    for (LatLng latLng : listPointsForPolygon) {
                        myLatLngList.add(new MyLatLng(latLng.latitude, latLng.longitude));
                    }

                    MyPolygon myPolygon = new MyPolygon(key, myLatLngList);
                    myPolygon.setUid(mFirebaseAuth.getCurrentUser().getUid());
                    myPolygon.setAuthor(mFirebaseAuth.getCurrentUser().getDisplayName());
                    myPolygon.setArea(SphericalUtil.computeArea(auxPolygonToShow.getPoints()));
                    mMapObjectsDatabaseReference.child("mapObjects").child(key).child("Polygon").setValue(myPolygon);

                    auxPolygonToShow.remove();

                    listPointsForPolygon.clear();
                    finishButton.setVisibility(View.GONE);
                    setInfoWindowFragmentListeners();
                }
            }
        });
    }

    private void setFinishPolylineButton(){
        final Button finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setText(R.string.txt4);
        finishButton.setVisibility(View.VISIBLE);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPointsForPolyline.size() >= 2) {
                    mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/" + task.getKey());
                    String key = mMapObjectsDatabaseReference.push().getKey();

                    List<MyLatLng> myLatLngList = new ArrayList<>();
                    for (LatLng latLng : listPointsForPolyline) {
                        myLatLngList.add(new MyLatLng(latLng.latitude, latLng.longitude));
                    }

                    MyPolyline myPolyline = new MyPolyline(key, myLatLngList);
                    myPolyline.setUid(mFirebaseAuth.getCurrentUser().getUid());
                    myPolyline.setAuthor(mFirebaseAuth.getCurrentUser().getDisplayName());
                    myPolyline.setLength(SphericalUtil.computeLength(auxPolylineToShow.getPoints()));
                    mMapObjectsDatabaseReference.child("mapObjects").child(key).child("Polyline").setValue(myPolyline);

                    auxPolylineToShow.remove();

                    listPointsForPolyline.clear();
                    finishButton.setVisibility(View.GONE);
                    setInfoWindowFragmentListeners();
                }
            }
        });
    }

    private void setFinishDeleteButton() {
        edit.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorPrimary)}));
        trash.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.buttonPressedDelete)}));
        final Button finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setText(R.string.txt3);
        finishButton.setVisibility(View.VISIBLE);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMap.setOnMarkerClickListener(null);
                myMap.setOnPolylineClickListener(null);
                myMap.setOnPolygonClickListener(null);
                finishButton.setVisibility(View.GONE);
                trash.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorPrimary)}));
                setInfoWindowFragmentListeners();



            }
        });
    }

    private List<LatLng> convertMyLatLngToLatLng(List<MyLatLng> myLatLngList){
        List<LatLng> latLngListToReturn = new ArrayList<>();
        for(MyLatLng myLatLng : myLatLngList){
            latLngListToReturn.add(new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude()));
        }
        return latLngListToReturn;
    }

    private void cancelPreviousIncompleteActions(){
        myMap.setOnMarkerClickListener(null);
        myMap.setOnPolygonClickListener(null);
        myMap.setOnPolylineClickListener(null);

        final Button finishButton = (Button) findViewById(R.id.finishButton);
        finishButton.setVisibility(View.GONE);

        edit.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorPrimary)}));
        trash.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorPrimary)}));

        if(auxPolygonToShow!=null){
            auxPolygonToShow.remove();
            auxPolygonToShow=null;
        }
        if(auxPolylineToShow!=null){
            auxPolylineToShow.remove();
            auxPolylineToShow=null;
        }
    }

    private void  setInfoWindowFragmentListeners(){
        myMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                if(!infoWindowFragment.isAdded()) {
                    infoWindowFragment = new InfoWindowFragment();

                    Bundle args = new Bundle();
                    args.putParcelable("MyPolyline", task.getPolylineById((String) polyline.getTag()));
                    infoWindowFragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.mMapView, infoWindowFragment)
                            .addToBackStack(null)
                            .commit();

                    myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            getSupportFragmentManager().beginTransaction().remove(infoWindowFragment).commit();
                        }
                    });
                }else{
                    getSupportFragmentManager().beginTransaction().remove(infoWindowFragment).commit();
                }
            }
        });

        myMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                if(!infoWindowFragment.isAdded()) {
                    infoWindowFragment = new InfoWindowFragment();

                    Bundle args = new Bundle();
                    args.putParcelable("MyPolygon", task.getPolygonById((String) polygon.getTag()));
                    infoWindowFragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.mMapView, infoWindowFragment)
                            .addToBackStack(null)
                            .commit();

                    myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            getSupportFragmentManager().beginTransaction().remove(infoWindowFragment).commit();
                        }
                    });
                }else{
                    getSupportFragmentManager().beginTransaction().remove(infoWindowFragment).commit();
                }
            }
        });
    }


}