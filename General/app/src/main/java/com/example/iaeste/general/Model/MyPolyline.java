package com.example.iaeste.general.Model;

import java.util.List;

/**
 * Created by franc on 14/8/2017.
 */

public class MyPolyline extends MapObject {

    private String id;
    private List<MyLatLng> points;
    private MyLatLng initialPoint;
    private MyLatLng finalPoint;

    public MyPolyline(){

    }

    public MyPolyline(String id, MyLatLng initialPoint, MyLatLng finalPoint){
        this.id = id;
        this.initialPoint = initialPoint;
        this.finalPoint = finalPoint;
    }

    public MyPolyline(String id, List<MyLatLng> points){
        this.id = id;
        this.points = points;
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

    public List<MyLatLng> getPoints() {
        return points;
    }

    public void setPoints(List<MyLatLng> points) {
        this.points = points;
    }
}
