package com.example.iaeste.general;

import android.Manifest;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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

import com.example.iaeste.general.Model.MyMarker;
import com.example.iaeste.general.Model.MyPolyline;
import com.example.iaeste.general.Model.MyLatLng;
import com.example.iaeste.general.Model.MyPolygon;
import com.example.iaeste.general.Model.MyUser;
import com.example.iaeste.general.Model.Task;
import com.example.iaeste.general.View.MyInfoWindow;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.SphericalUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements LocationListener {

    private GoogleMap myMap;
    private ProgressDialog myProgress;
    private static final String MYTAG = "MYTAG";
    FloatingActionButton addObj_3, addObj_2, addObj_1, addObj, trash, edit, camera, location, marker1, camera1;
    Animation FabOpen, FabClose, FabClockWise, Fabanticlockwise;
    boolean isOpen = false;
    boolean isOpen1 = false;


    // Request Code to ask the user for permission to view their current location (***).
    // Value 8bit (value <256)
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    private static final int CAMERA_PIC_REQUEST = 1337;

    private Task task;
    private List<MyUser> userListTask;
    private List<LatLng> listPointsForPolyline = new ArrayList<>();
    private List<LatLng> listPointsForPolygon = new ArrayList<>();

    private HashMap<String, Marker> usersLocationMarkers = new HashMap<>();

    private HashMap<String, Marker> markerHashMap = new HashMap<>();
    private HashMap<String, Polyline> polylineHashMap = new HashMap<>();
    private HashMap<String, Polygon> polygonHashMap = new HashMap<>();
    private Polygon auxPolygonToShow;
    private Polyline auxPolylineToShow;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mMapObjectsDatabaseReference;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mUsersDatabaseReference;
    private ChildEventListener mUserChildPositionListener ;

    InfoWindowFragment infoWindowFragment;

    private String imageId;

    private Location myLocation = null;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent(); // gets the previously created intent
        task = (Task) intent.getParcelableExtra("task");

        userListTask = new ArrayList<>();
        userListTask = intent.getParcelableArrayListExtra("userListTask");

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

        userLastKnownPositionInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        myMap.setInfoWindowAdapter(new MyInfoWindow(getLayoutInflater(), this));

        setInfoWindowFragmentListeners();

        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (infoWindowFragment.isAdded()) {
                    getSupportFragmentManager().beginTransaction().remove(infoWindowFragment).commit();
                }
            }
        });

    }


    private void firebaseDatabaseInit() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/" + task.getKey() + "/mapObjects/");

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("New element", markerChild.toString());
                    //Punto
                    if (markerChild.getKey().equals("MyMarker")) {
                        MyMarker newMyMarker = markerChild.getValue(MyMarker.class);
                        addPoint(newMyMarker);
                    }

                    //Linea
                    if (markerChild.getKey().equals("Polyline")) {
                        MyPolyline newMyPolyline = markerChild.getValue(MyPolyline.class);
                        addPolyline(newMyPolyline);
                    }

                    //Poligono
                    if (markerChild.getKey().equals("Polygon")) {
                        MyPolygon newPolygon = markerChild.getValue(MyPolygon.class);
                        addPolygon(newPolygon);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("Element modified", markerChild.toString());
                    //Punto
                    if (markerChild.getKey().equals("MyMarker")) {
                        MyMarker myMarkerModified = markerChild.getValue(MyMarker.class);
                        removePoint(myMarkerModified);
                        addPoint(myMarkerModified);
                    }

                    //Linea
                    if (markerChild.getKey().equals("Polyline")) {
                        MyPolyline myPolylineModified = markerChild.getValue(MyPolyline.class);
                        removePolyline(myPolylineModified);
                        addPolyline(myPolylineModified);
                    }

                    //Poligono
                    if (markerChild.getKey().equals("Polygon")) {
                        MyPolygon myPolygonModified = markerChild.getValue(MyPolygon.class);
                        removePolygon(myPolygonModified);
                        addPolygon(myPolygonModified);
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
                    Log.e("Element to delete", markerChild.toString());

                    //Punto
                    if (markerChild.getKey().equals("MyMarker")) {
                        MyMarker myMarkerToRemove = markerChild.getValue(MyMarker.class);
                        removePoint(myMarkerToRemove);
                        Toast.makeText(MapsActivity.this, R.string.myMarker, Toast.LENGTH_SHORT).show();
                    }

                    //Linea
                    if (markerChild.getKey().equals("Polyline")) {
                        MyPolyline myPolylineToRemove = markerChild.getValue(MyPolyline.class);
                        removePolyline(myPolylineToRemove);
                        Toast.makeText(MapsActivity.this, R.string.polyline, Toast.LENGTH_SHORT).show();
                    }

                    //Poligono
                    if (markerChild.getKey().equals("Polygon")) {
                        MyPolygon polygonToRemove = markerChild.getValue(MyPolygon.class);
                        removePolygon(polygonToRemove);
                        Toast.makeText(MapsActivity.this, R.string.polygon, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot markerChild : dataSnapshot.getChildren()) {
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

    private void userLastKnownPositionInit(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference("users");

        mUserChildPositionListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println(dataSnapshot.toString());
                if(!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    MyUser myUser = dataSnapshot.getValue(MyUser.class);
                    if(contains(userListTask, myUser)){
                        myUser.setUid(dataSnapshot.getKey());
                        for (DataSnapshot userChild : dataSnapshot.getChildren()) {
                            if (userChild.getKey().equals("lastKnownLocation")) {
                                double latitude = (double) userChild.child("latitude").getValue();
                                double longitude = (double) userChild.child("longitude").getValue();
                                updateUserLastKnwonLocation(myUser, latitude, longitude);
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println(dataSnapshot.toString());
                if(!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    MyUser myUser = dataSnapshot.getValue(MyUser.class);
                    myUser.setUid(dataSnapshot.getKey());
                    for (DataSnapshot userChild : dataSnapshot.getChildren()) {
                        if (userChild.getKey().equals("lastKnownLocation")) {
                            double latitude = (double) userChild.child("latitude").getValue();
                            double longitude = (double) userChild.child("longitude").getValue();
                            updateUserLastKnwonLocation(myUser, latitude, longitude);
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mUsersDatabaseReference.addChildEventListener(mUserChildPositionListener);
    }

    private boolean contains(List<MyUser> userListTask, MyUser myUser) {
        boolean find = false; int i=0;
        while(i<userListTask.size() && !find){
            if(userListTask.get(i).getUid().equals(myUser.getUid())){
                find=true;
            }
            i++;
        }
        return find;
    }

    private void updateUserLastKnwonLocation(MyUser myUser, double latitude, double longitude) {
        System.out.println(myUser.getDisplayName());
        LatLng latLng = new LatLng(latitude, longitude);
        if(usersLocationMarkers.containsKey(myUser.getUid())){
            Marker marker = usersLocationMarkers.get(myUser.getUid());
            marker.setPosition(latLng);
        }else{
            Marker newMarker = myMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_person_pin_black_48px))
                    .title(myUser.getDisplayName())
            );
            usersLocationMarkers.put(myUser.getUid(), newMarker);
        }
    }

    private void addPoint(MyMarker myMarker) {
        Marker newMarker;
        if (myMarker.getImageId() == null) {
            newMarker = myMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(myMarker.getPosition().getLatitude(), myMarker.getPosition().getLongitude()))
                            .title(myMarker.getTitle())
                            .snippet(myMarker.getAuthor() + "/&" + myMarker.getDescription())
            );
        } else {
            newMarker = myMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(myMarker.getPosition().getLatitude(), myMarker.getPosition().getLongitude()))
                            .icon(bitmapDescriptorFromVector(this, R.drawable.marker_camera))
                            .snippet("image")
            );
        }
        newMarker.setTag(myMarker.getId());
        markerHashMap.put(myMarker.getId(), newMarker);
        task.getMyMarkerList().add(myMarker);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void removePoint(MyMarker myMarker) {
        Marker markerToRemove = markerHashMap.get(myMarker.getId());
        markerToRemove.remove();
        markerHashMap.remove(markerToRemove);
        task.getMyMarkerList().remove(myMarker);
    }

    private void addPolyline(MyPolyline myPolyline) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(convertMyLatLngToLatLng(myPolyline.getPoints()));

        Polyline newPolyline = myMap.addPolyline(polylineOptions);
        newPolyline.setClickable(true);
        newPolyline.setTag(myPolyline.getId());

        polylineHashMap.put(myPolyline.getId(), newPolyline);
        task.getPolylineList().add(myPolyline);
    }

    private void removePolyline(MyPolyline myPolyline) {
        Polyline polylineToRemove = polylineHashMap.get(myPolyline.getId());
        polylineToRemove.remove();
        polylineHashMap.remove(polylineToRemove);
        task.getPolylineList().remove(polylineToRemove);
    }

    private void addPolygon(MyPolygon myPolygon) {
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(convertMyLatLngToLatLng(myPolygon.getVertices()));

        Polygon newPolygon = myMap.addPolygon(polygonOptions);
        newPolygon.setClickable(true);
        newPolygon.setTag(myPolygon.getId());

        polygonHashMap.put(myPolygon.getId(), newPolygon);
        task.getPolygonList().add(myPolygon);
    }

    private void removePolygon(MyPolygon myPolygon) {
        Polygon polygonToRemove = polygonHashMap.get(myPolygon.getId());
        polygonToRemove.remove();
        polygonHashMap.remove(polygonToRemove);
        task.getPolygonList().remove(polygonToRemove);
    }

    private void deletePointFromFirebase(Marker marker) {
        String key = (String) marker.getTag();
        mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/" + task.getKey() + "/mapObjects/");
        mMapObjectsDatabaseReference.child(key).removeValue();
    }

    private void deleteLineFromFirebase(Polyline polyline) {
        String key = (String) polyline.getTag();
        mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/" + task.getKey() + "/mapObjects/");
        mMapObjectsDatabaseReference.child(key).removeValue();
    }

    private void deletePolygonFromFirebase(Polygon polygon) {
        String key = (String) polygon.getTag();
        mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/" + task.getKey() + "/mapObjects/");
        mMapObjectsDatabaseReference.child(key).removeValue();
    }

    private void deleteImageFromFirebase(Marker marker) {
        String imageId = (String) marker.getTag();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference("/images/" + imageId + ".jpg");
        imageRef.delete();
    }

    public void Dot(View view) {
        cancelPreviousIncompleteActions();
        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/" + task.getKey());
                String key = mMapObjectsDatabaseReference.push().getKey();
                MyMarker newMyMarker = new MyMarker(key,
                        new MyLatLng(point.latitude, point.longitude));
                newMyMarker.setUid(mFirebaseAuth.getCurrentUser().getUid());
                newMyMarker.setAuthor(mFirebaseAuth.getCurrentUser().getDisplayName());
                mMapObjectsDatabaseReference.child("mapObjects").child(key).child("MyMarker").setValue(newMyMarker);
            }

        });
    }

    public void Multiline(View view) {
        cancelPreviousIncompleteActions();
        listPointsForPolyline.clear();
        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                listPointsForPolyline.add(point);
                if (auxPolylineToShow != null) {
                    auxPolylineToShow.remove();
                }
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(listPointsForPolyline);
                auxPolylineToShow = myMap.addPolyline(polylineOptions);

                if (listPointsForPolyline.size() == 2) {
                    setFinishPolylineButton();
                }
            }
        });
    }

    public void Polygon(View view) {
        cancelPreviousIncompleteActions();
        listPointsForPolygon.clear();
        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                listPointsForPolygon.add(point);
                if (auxPolygonToShow != null) {
                    auxPolygonToShow.remove();
                }
                PolygonOptions polygonOptions = new PolygonOptions();
                polygonOptions.addAll(listPointsForPolygon);
                auxPolygonToShow = myMap.addPolygon(polygonOptions);

                if (listPointsForPolygon.size() == 3) {
                    setFinishPolygonButton();
                }
            }
        });
    }

    public void Picture(View view) {
        cancelPreviousIncompleteActions();
        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/" + task.getKey());
                String key = mMapObjectsDatabaseReference.push().getKey();
                MyMarker newMyMarker = new MyMarker(key,
                        new MyLatLng(point.latitude, point.longitude));
                newMyMarker.setUid(mFirebaseAuth.getCurrentUser().getUid());
                newMyMarker.setAuthor(mFirebaseAuth.getCurrentUser().getDisplayName());
                newMyMarker.setImageId(key);
                imageId = key;
                mMapObjectsDatabaseReference.child("mapObjects").child(key).child("MyMarker").setValue(newMyMarker);
                addCameraPhoto();
            }

        });

    }

    public void PictureActualPosition(View view) {
        cancelPreviousIncompleteActions();
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
        String locationProvider = this.getEnabledLocationProvider();
        if (locationProvider == null) {
            return;
        }

        LatLng latLng = new LatLng(
                locationManager.getLastKnownLocation(locationProvider).getLatitude(),
                locationManager.getLastKnownLocation(locationProvider).getLongitude());

        mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/" + task.getKey());
        String key = mMapObjectsDatabaseReference.push().getKey();
        MyMarker newMyMarker = new MyMarker(key,
                new MyLatLng(latLng.latitude, latLng.longitude));
        newMyMarker.setUid(mFirebaseAuth.getCurrentUser().getUid());
        newMyMarker.setAuthor(mFirebaseAuth.getCurrentUser().getDisplayName());
        newMyMarker.setImageId(key);
        imageId = key;
        mMapObjectsDatabaseReference.child("mapObjects").child(key).child("MyMarker").setValue(newMyMarker);
        addCameraPhoto();
    }

    public void MarkerActualPosition(View view) {
        cancelPreviousIncompleteActions();
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
        String locationProvider = this.getEnabledLocationProvider();
        if (locationProvider == null) {
            return;
        }

        LatLng latLng = new LatLng(
                locationManager.getLastKnownLocation(locationProvider).getLatitude(),
                locationManager.getLastKnownLocation(locationProvider).getLongitude());

        mMapObjectsDatabaseReference = mFirebaseDatabase.getReference("/task/" + task.getKey());
        String key = mMapObjectsDatabaseReference.push().getKey();
        MyMarker newMyMarker = new MyMarker(key,
                new MyLatLng(latLng.latitude, latLng.longitude));
        newMyMarker.setUid(mFirebaseAuth.getCurrentUser().getUid());
        newMyMarker.setAuthor(mFirebaseAuth.getCurrentUser().getDisplayName());
        mMapObjectsDatabaseReference.child("mapObjects").child(key).child("MyMarker").setValue(newMyMarker);
    }

    public void addCameraPhoto () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_PIC_REQUEST);
            }else{
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        }else{
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_PIC_REQUEST) {
            if(data != null) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                uploadImageToFirebaseStorage(image);
            }
        }
    }

    private void uploadImageToFirebaseStorage(Bitmap image){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference("/images/"+imageId+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MapsActivity.this, "La imagen NO se guardo en firebase", Toast.LENGTH_SHORT);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Toast.makeText(MapsActivity.this, "La imagen se guardo en firebase!", Toast.LENGTH_SHORT);
              //  Uri downloadUrl = taskSnapshot.getDownloadUrl();
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
                if(marker.getSnippet().equals("image")){
                    deleteImageFromFirebase(marker);
                }
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
            case CAMERA_PIC_REQUEST: {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
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

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProvider = this.getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

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

        } else {
            Toast.makeText(this, R.string.toast11, Toast.LENGTH_LONG).show();
            Log.i(MYTAG, "Location not found");
        }

    }


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
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mActualUsersDatabaseReference = mFirebaseDatabase.getReference("/users/"+mFirebaseAuth.getCurrentUser().getUid());
        mActualUsersDatabaseReference.child("lastKnownLocation").child("latitude").setValue(location.getLatitude());
        mActualUsersDatabaseReference.child("lastKnownLocation").child("longitude").setValue(location.getLongitude());
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
        camera = (FloatingActionButton) findViewById(R.id.camera);
        location = (FloatingActionButton) findViewById(R.id.location);
        camera1 = (FloatingActionButton) findViewById(R.id.camera1);
        marker1 = (FloatingActionButton) findViewById(R.id.marker1);
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
                    camera.startAnimation(FabClose);
                    location.startAnimation(FabClose);
                    marker1.startAnimation(FabClose);
                    camera1.startAnimation(FabClose);
                    addObj.startAnimation(Fabanticlockwise);
                    addObj_1.setClickable(false);
                    addObj_2.setClickable(false);
                    addObj_3.setClickable(false);
                    camera.setClickable(false);
                    location.setClickable(false);
                    camera1.setClickable(false);
                    marker1.setClickable(false);
                    trash.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                    myMap.setOnMapClickListener(null);
                    setInfoWindowFragmentListeners();
                    isOpen = false;
                } else {
                    trash.setVisibility(View.GONE);
                    edit.setVisibility(View.GONE);
                    addObj_1.startAnimation(FabOpen);
                    addObj_2.startAnimation(FabOpen);
                    addObj_3.startAnimation(FabOpen);
                    camera.startAnimation(FabOpen);
                    location.startAnimation(FabOpen);
                    addObj.startAnimation(FabClockWise);
                    addObj_1.setClickable(true);
                    addObj_2.setClickable(true);
                    addObj_3.setClickable(true);
                    camera.setClickable(true);
                    location.setClickable(true);
                    camera1.setClickable(false);
                    marker1.setClickable(false);
                    isOpen = true;
                    location.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(isOpen) {
                                if (isOpen1) {
                                    marker1.startAnimation(FabClose);
                                    camera1.startAnimation(FabClose);
                                    camera1.setClickable(false);
                                    marker1.setClickable(false);
                                    isOpen1 = false;
                                } else {
                                    marker1.startAnimation(FabOpen);
                                    camera1.startAnimation(FabOpen);
                                    camera1.setClickable(true);
                                    marker1.setClickable(true);
                                    isOpen1 = true;
                                }
                            }

                        }
                    });
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

        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if((marker.getSnippet() != null) && marker.getSnippet().equals("image")){
                    if(!infoWindowFragment.isAdded()) {
                        MyMarker myMarker = task.getPointById((String) marker.getTag());

                        infoWindowFragment = new InfoWindowFragment();

                        Bundle args = new Bundle();
                        args.putParcelable("PointPicture", myMarker);
                        infoWindowFragment.setArguments(args);

                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.mMapView, infoWindowFragment)
                                .addToBackStack(null)
                                .commit();
                    }else {
                        getSupportFragmentManager().beginTransaction().remove(infoWindowFragment).commit();
                    }

                }
                return false;
            }
        });
    }


}