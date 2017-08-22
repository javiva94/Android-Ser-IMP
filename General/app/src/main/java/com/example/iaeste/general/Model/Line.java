package com.example.iaeste.general.Model;

/**
 * Created by franc on 14/8/2017.
 */

public class Line extends MapObject {

    private String id;
    private LatLng initialPoint;
    private LatLng finalPoint;

    public Line(){

    }

    public Line(String id, LatLng initialPoint, LatLng finalPoint){
        this.id = id;
        this.initialPoint = initialPoint;
        this.finalPoint = finalPoint;
    }

    public String getId() {
        return id;
    }

    public LatLng getInitialPoint() {
        return initialPoint;
    }

    public LatLng getFinalPoint() {
        return finalPoint;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInitialPoint(LatLng initialPoint) {
        this.initialPoint = initialPoint;
    }

    public void setFinalPoint(LatLng finalPoint) {
        this.finalPoint = finalPoint;
    }
}
