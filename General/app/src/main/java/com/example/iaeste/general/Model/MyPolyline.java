package com.example.iaeste.general.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by franc on 14/8/2017.
 */

public class MyPolyline extends MapObject implements Parcelable{

    private String id;
    private List<MyLatLng> points;
    private String title;
    private String description;
    private String author;
    private double length;

    public MyPolyline(){

    }

    public MyPolyline(String id, List<MyLatLng> points){
        this.id = id;
        this.points = points;
    }

    protected MyPolyline(Parcel in) {
        id = in.readString();
        points = in.createTypedArrayList(MyLatLng.CREATOR);
        title = in.readString();
        description = in.readString();
        author = in.readString();
        length = in.readDouble();
    }

    public static final Creator<MyPolyline> CREATOR = new Creator<MyPolyline>() {
        @Override
        public MyPolyline createFromParcel(Parcel in) {
            return new MyPolyline(in);
        }

        @Override
        public MyPolyline[] newArray(int size) {
            return new MyPolyline[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("points", points);
        result.put("title", title);
        result.put("description", description);
        result.put("author", author);
        result.put("length", length);

        return result;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MyLatLng> getPoints() {
        return points;
    }

    public void setPoints(List<MyLatLng> points) {
        this.points = points;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeTypedList(points);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(author);
        dest.writeDouble(length);
    }
}
