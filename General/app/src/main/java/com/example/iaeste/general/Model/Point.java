package com.example.iaeste.general.Model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by franc on 14/8/2017.
 */

@IgnoreExtraProperties
public class Point extends MapObject implements Parcelable {

    private LatLng point;

    public Point(){

    }

    public Point(LatLng point){
        this.point = point;
    }

    protected Point(Parcel in) {
        point = in.readParcelable(LatLng.class.getClassLoader());
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
        result.put("point", point);

        return result;
    }

    @Override
    public LatLng getLatLng() {
        return point;
    }

    public void setPoint(LatLng point) {
        this.point = point;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.point, flags);
    }
}
