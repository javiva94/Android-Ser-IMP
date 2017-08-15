package com.example.iaeste.general.Model;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

@IgnoreExtraProperties
public class Task implements Serializable {

    private String taskKey;
    private List<MapObject> mapObjectsList = new ArrayList<>();
    private String title;
    private String description;

    public Task(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Task(String title){
        this.title = title;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        //result.put("uid", uid);
        //result.put("author", author);
        result.put("title", title);
        result.put("mapObjects", mapObjectsList);

        return result;
    }

    public void setTaskKey(String id){ taskKey = id; }

    public String getTaskKey() { return taskKey; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public List<MapObject> getMapObjectsList() {
        return mapObjectsList;
    }

    public void setMapObjectsList(List<MapObject> mapObjectsList) {
        this.mapObjectsList = mapObjectsList;
    }

    public void addMapObjectToTask(MapObject mapObject){
        mapObjectsList.add(mapObject);
    }
/*
    public ListIterator<MapObject> getMapObjectsFromTask(){
       return mapObjectsList.listIterator();
    }
*/

}
