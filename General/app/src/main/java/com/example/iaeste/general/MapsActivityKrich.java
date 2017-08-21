package com.example.iaeste.general;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivityKrich extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback{
      //  ActivityCompat.OnRequestPermissionsResultCallback {

    DatabaseHelper mDatabaseHelper;
    EditText titleArea;
    GoogleMap mMapView;
    private List<LatLng> listOfLatLng = new ArrayList<>();
    private Polygon polygon;
    Marker marker;
    EditText search_field;
    // TextView Tv_result;


    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Tv_result = (TextView)findViewById(R.id.TV_result);
        mDatabaseHelper = new DatabaseHelper(this);

        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.mMapView);
        mapFragment.getMapAsync(this);
/*
        if (googleServiceAvaliable()) {
            Toast.makeText(this, "Successful", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_maps);
        }

        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Menu_typeNone :
                mMapView.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.Menu_typeNormal :
                mMapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.Menu_typeTerrain :
                mMapView.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.Menu_typeSatellite :
                mMapView.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.Menu_typeHybrid:
                mMapView.setMapType(GoogleMap.MAP_TYPE_HYBRID);
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

    public boolean googleServiceAvaliable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvaliable = api.isGooglePlayServicesAvailable(this);
        if (isAvaliable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvaliable)) {
            Dialog dialog = api.getErrorDialog(this, isAvaliable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot connect to play store", Toast.LENGTH_LONG).show();
        }
        return false;
    }
    public void geolocate(View view) throws IOException {

        if(marker!=null)
            marker.remove();

        search_field = (EditText)findViewById(R.id.Tv_program);
        String location = search_field.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location,1);
        Address address = list.get(0);
        String locality = address.getLocality();

        // Toast.makeText(this, locality,Toast.LENGTH_LONG).show();
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        goToLocationZoom(lat,lng,15);
        setMarker(lat,lng,locality);
        //  Toast.makeText(this, "You have to clear pin before pining area",Toast.LENGTH_LONG).show();

    }
    private void setMarker(double lat, double lng, String locality) {
        // Toast.makeText(this, locality,Toast.LENGTH_LONG).show();
        LatLng latLng = new LatLng(lat, lng);
        marker = mMapView.addMarker(new MarkerOptions().position(latLng).title(locality)
                .snippet(""));
        mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void bindWidget() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mMapView);
        mapFragment.getMapAsync(this);
    }
    private void drawRealtimePolygon(LatLng latLng) {
        listOfLatLng.add(latLng);
        if (listOfLatLng.size() > 2) {
            if (polygon != null) {
                polygon.remove();
            }
            polygon = mMapView.addPolygon(new PolygonOptions()
                    .addAll(listOfLatLng)
                    .strokeColor(Color.parseColor("#3978DD"))
                    .fillColor(Color.parseColor("#773978DD")));
            polygon.setStrokeWidth(4);

            toastAreaInPolygon(polygon.getPoints());
            toastCenter(listOfLatLng);
        }
        marker = mMapView.addMarker(new MarkerOptions().position(latLng));
    }

    private void toastCenter(List<LatLng> listOfLatLng) {
        LatLng a;
        a = getCenterOfPolygon(listOfLatLng);
        double lat = a.latitude;
        double lng = a.longitude;
        String lat1 = Double.toString(lat);
        String lng1 = Double.toString(lng);
        Toast.makeText(this,"Lat is "+lat1+"Lng is "+lng1,Toast.LENGTH_LONG).show();
    }


    private void toastAreaInPolygon(final List<LatLng> latLngs){
        // calculate area in polygon
        double sizeInSquareMeters = CMMapUtil.calculatePolygonArea(polygon.getPoints());
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        Toast.makeText(getApplicationContext(),formatter.format(sizeInSquareMeters/1000)
                + " kilometer²", Toast.LENGTH_LONG).show();
    }
    /*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapView = googleMap;

        if( mMapView!=null){
            mMapView.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                }
            });
            mMapView.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    // setMarker(latLng.latitude,latLng.longitude,"");
                    drawRealtimePolygon(latLng);

                }
            });
        }

        LatLng dorm = new LatLng(44.813567, 20.483488);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(dorm, 15.0f);
        mMapView.moveCamera(update);
        mMapView.getUiSettings().setZoomControlsEnabled(true);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},0 );
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMapView.setMyLocationEnabled(true);
        mMapView.getUiSettings().setMyLocationButtonEnabled(true);
        //   mMapView.setTrafficEnabled(true);

        mMapView.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.marker_info_content, null);
                TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);
                if (marker.getTitle() != null && !marker.getTitle().equals("")) {
                    tvTitle.setText(marker.getTitle());
                    tvTitle.setVisibility(View.VISIBLE);
                }else{
                    tvTitle.setVisibility(View.GONE);
                }
                LatLng latLng = marker.getPosition();
                TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
                TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
                DecimalFormat formatter = new DecimalFormat("#,###.000");

                tvLat.setText("Latitude: " + formatter.format(latLng.latitude) + "°");
                tvLng.setText("Longtitude: " + formatter.format(latLng.longitude) + "°");

                return v;
            }
        });

    }*/

    @Override
    public void onMapReady(GoogleMap map) {
        mMapView = map;

        mMapView.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMapView != null) {
            // Access to the location has been granted to the app.
            mMapView.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode ==0){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                mMapView.setMyLocationEnabled(true);
            }
        }
    }
    */
    private void goToLocationZoom(double lat, double lng, float zoom){
        LatLng locate = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(locate,zoom);
        mMapView.moveCamera(update);
    }

    public void Type(View view) {
        ForestType my_dialog = new ForestType();
        my_dialog.show(getFragmentManager(),"MyTag");
    }

    public void Clear(View view) {
        if(marker!=null)
            marker.remove();
        if (polygon != null) {
            polygon=null;
            // polygon.remove();
            mMapView.clear();
            marker.remove();
            listOfLatLng.clear();
        }
        else
            return;
    }
    private static LatLng getCenterOfPolygon(List<LatLng> latLngList) {
        double[] centroid = {0.0, 0.0};
        for (int i = 0; i < latLngList.size(); i++) {
            centroid[0] += latLngList.get(i).latitude;
            centroid[1] += latLngList.get(i).longitude;
        }
        int totalPoints = latLngList.size();
        return new LatLng(centroid[0] / totalPoints, centroid[1] / totalPoints);
    }

    public void OnClickSave(View v) {
        if (polygon != null) {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapsActivityKrich.this);
            View mview = getLayoutInflater().inflate(R.layout.dialog_title, null);
            titleArea = (EditText) mview.findViewById(R.id.TitleArea);

            mBuilder.setPositiveButton("Summit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!titleArea.getText().toString().isEmpty()) {
                        Toast.makeText(MapsActivityKrich.this, "Title : " + titleArea.getText(), Toast.LENGTH_SHORT).show();
                        //String newEntry =  titleArea.getText().toString();
                        //addData(newEntry);
                        titleArea.setText("");
                    } else
                        Toast.makeText(MapsActivityKrich.this, "Unsuccessful. Please fill any titles again", Toast.LENGTH_SHORT).show();
                }
            });

            mBuilder.setView(mview);
            AlertDialog dialog = mBuilder.create();
            dialog.show();
        }
        else{
            Toast.makeText(MapsActivityKrich.this, "Cannot be saved, Please pin the area",Toast.LENGTH_LONG).show();
        }
    }
    public void addData(String newEntry){
        boolean insertData = mDatabaseHelper.addData(newEntry);
        if(insertData)
            toastMsg("Data successfully inserted");
        else
            toastMsg("Something went wrong");
    }
    private void toastMsg(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

}
