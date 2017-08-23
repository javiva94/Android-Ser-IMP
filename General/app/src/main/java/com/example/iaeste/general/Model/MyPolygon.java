package com.example.iaeste.general.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 14/8/2017.
 */

public class MyPolygon extends MapObject {

    private String id;
    private List<MyLatLng> vertices = new ArrayList<>();

    public MyPolygon(){

    }

    public MyPolygon(String id, List<MyLatLng> vertices){
        this.id = id;
        this.vertices = vertices;
    }

    public String getId() {
        return id;
    }

    public List<MyLatLng> getVertices() {
        return vertices;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVertices(List<MyLatLng> vertices) {
        this.vertices = vertices;
    }
}
