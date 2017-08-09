package com.example.iaeste.general;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by franc on 9/8/2017.
 */

public class Task {

    private List<Marker> markerList;
    private String title;
    private String description;

    public Task(String title, String description){
        markerList = new ArrayList<Marker>();
        this.title = title;
        this.description= description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
