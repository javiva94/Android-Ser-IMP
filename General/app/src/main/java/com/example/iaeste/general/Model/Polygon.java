package com.example.iaeste.general.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 14/8/2017.
 */

public class Polygon extends MapObject {

    private String id;
    private List<LatLng> vertices = new ArrayList<>();

    public Polygon(){

    }

    public Polygon(String id, List<LatLng> vertices){
        this.id = id;
        this.vertices = vertices;
    }

    public String getId() {
        return id;
    }

    public List<LatLng> getVertices() {
        return vertices;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVertices(List<LatLng> vertices) {
        this.vertices = vertices;
    }
}
