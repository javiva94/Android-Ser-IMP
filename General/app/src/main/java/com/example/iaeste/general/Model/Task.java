package com.example.iaeste.general.Model;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@IgnoreExtraProperties
public class Task {

    private int taskId;
    private List<Marker> markerList;
    private String title;
    private String description;

    public Task(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Task(String title){
        markerList = new ArrayList<Marker>();
        this.title = title;
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
