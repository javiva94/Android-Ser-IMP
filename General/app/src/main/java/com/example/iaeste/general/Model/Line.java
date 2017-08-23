package com.example.iaeste.general.Model;

/**
 * Created by franc on 14/8/2017.
 */

public class Line extends MapObject {

    private String id;
    private MyLatLng initialPoint;
    private MyLatLng finalPoint;

    public Line(){

    }

    public Line(String id, MyLatLng initialPoint, MyLatLng finalPoint){
        this.id = id;
        this.initialPoint = initialPoint;
        this.finalPoint = finalPoint;
    }

    public String getId() {
        return id;
    }

    public MyLatLng getInitialPoint() {
        return initialPoint;
    }

    public MyLatLng getFinalPoint() {
        return finalPoint;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInitialPoint(MyLatLng initialPoint) {
        this.initialPoint = initialPoint;
    }

    public void setFinalPoint(MyLatLng finalPoint) {
        this.finalPoint = finalPoint;
    }
}
