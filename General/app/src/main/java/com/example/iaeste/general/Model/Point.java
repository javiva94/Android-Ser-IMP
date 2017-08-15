package com.example.iaeste.general.Model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by franc on 14/8/2017.
 */

public class Point extends MapObject {

    private LatLng point;

    public Point(LatLng latLng){
        this.point = latLng;
    }

    public LatLng getLatLng() {
        return point;
    }

    public void setLatLng(LatLng point) {
        this.point = point;
    }
}
