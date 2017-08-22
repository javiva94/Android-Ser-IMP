package com.example.iaeste.general.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.GenericTypeIndicator;
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
public class Task implements Parcelable {

    @Exclude
    private String key;

  //  private List<MapObject> mapObjects = new ArrayList<>();
    private List<Point> pointList = new ArrayList<>();
    private List<Line> lineList = new ArrayList<>();
    private List<MyPolygon> polygonList = new ArrayList<>();
    private String title;
    private String description;


    public Task(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    protected Task(Parcel in) {
        key = in.readString();
        title = in.readString();
        in.readList(pointList, getClass().getClassLoader());
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        //result.put("uid", uid);
        //result.put("author", author);
        result.put("title", title);
        result.put("pointList", pointList);

        return result;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

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

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    public List<Line> getLineList() {
        return lineList;
    }

    public void setLineList(List<Line> lineList) {
        this.lineList = lineList;
    }

    public List<MyPolygon> getPolygonList() {
        return polygonList;
    }

    public void setPolygonList(List<MyPolygon> polygonList) {
        this.polygonList = polygonList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(title);
        dest.writeList(pointList);
    }

}
