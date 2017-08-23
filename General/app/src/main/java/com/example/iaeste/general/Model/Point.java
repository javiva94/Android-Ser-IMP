package com.example.iaeste.general.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Point extends MapObject implements Parcelable {

    private String id;
    private MyLatLng position;

    public Point(){

    }

    public Point(String id, MyLatLng position){
        this.id = id;
        this.position = position;
    }


    protected Point(Parcel in) {
        id = in.readString();
        position = in.readParcelable(MyLatLng.class.getClassLoader());
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        //result.put("uid", uid);
        //result.put("author", author);
        result.put("id", id);
        result.put("position", position);

        return result;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MyLatLng getPosition() {
        return position;
    }

    public void setPosition(MyLatLng position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(this.position, flags);
    }


    @Override
    public String toString() {
        return "Key: "+id+" - Latitude: "+position.getLatitude()+" Longitude: "+position.getLongitude();
    }
}
