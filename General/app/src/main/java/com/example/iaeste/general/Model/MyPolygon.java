package com.example.iaeste.general.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 14/8/2017.
 */

public class MyPolygon extends MapObject implements Parcelable{

    private String id;

    private List<MyLatLng> vertices = new ArrayList<>();

    public MyPolygon(){

    }

    public MyPolygon(String id, List<MyLatLng> vertices){
        this.id = id;
        this.vertices = vertices;
    }

    protected MyPolygon(Parcel in) {
        id = in.readString();
        vertices = in.createTypedArrayList(MyLatLng.CREATOR);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeTypedList(vertices);
    }
}
