package com.example.iaeste.general.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by franc on 14/8/2017.
 */

public class MyPolygon extends MapObject implements Parcelable{

    private String id;
    private List<MyLatLng> vertices = new ArrayList<>();
    private String title;
    private String description;
    private String author;
    private double area;
    private String uid;

    public MyPolygon(){

    }

    public MyPolygon(String id, List<MyLatLng> vertices){
        this.id = id;
        this.vertices = vertices;
    }

    protected MyPolygon(Parcel in) {
        id = in.readString();
        vertices = in.createTypedArrayList(MyLatLng.CREATOR);
        title = in.readString();
        description = in.readString();
        author = in.readString();
        area = in.readDouble();
        uid = in.readString();
    }

    public static final Creator<MyPolygon> CREATOR = new Creator<MyPolygon>() {
        @Override
        public MyPolygon createFromParcel(Parcel in) {
            return new MyPolygon(in);
        }

        @Override
        public MyPolygon[] newArray(int size) {
            return new MyPolygon[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("vertices", vertices);
        result.put("title", title);
        result.put("description", description);
        result.put("author", author);
        result.put("area", area);
        result.put("uid", uid);

        return result;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MyLatLng> getVertices() {
        return vertices;
    }

    public void setVertices(List<MyLatLng> vertices) {
        this.vertices = vertices;
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

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeTypedList(vertices);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(author);
        dest.writeDouble(area);
        dest.writeString(uid);
    }
}
