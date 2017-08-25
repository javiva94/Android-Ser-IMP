package com.example.iaeste.general.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by franc on 14/8/2017.
 */

public class MyPolyline extends MapObject implements Parcelable{

    private String id;
    private List<MyLatLng> points;

    public MyPolyline(){

    }

    public MyPolyline(String id, List<MyLatLng> points){
        this.id = id;
        this.points = points;
    }

    protected MyPolyline(Parcel in) {
        id = in.readString();
        points = in.createTypedArrayList(MyLatLng.CREATOR);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeTypedList(points);
    }
}
