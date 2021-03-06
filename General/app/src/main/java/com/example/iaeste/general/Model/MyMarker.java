package com.example.iaeste.general.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class MyMarker extends MapObject implements Parcelable {

    private String id;
    private MyLatLng position;
    private String title;
    private String description;
    private String author;
    private String uid;
    private String imageId;

    public MyMarker(){

    }

    public MyMarker(String id, MyLatLng position){
        this.id = id;
        this.position = position;
        this.title = "";
        this.description = "";
    }


    protected MyMarker(Parcel in) {
        id = in.readString();
        position = in.readParcelable(MyLatLng.class.getClassLoader());
        title = in.readString();
        description = in.readString();
        author = in.readString();
        uid = in.readString();
        imageId = in.readString();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("position", position);
        result.put("title", title);
        result.put("description", description);
        result.put("author", author);
        result.put("uid", uid);
        result.put("imageId", imageId);

        return result;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static final Creator<MyMarker> CREATOR = new Creator<MyMarker>() {
        @Override
        public MyMarker createFromParcel(Parcel in) {
            return new MyMarker(in);
        }

        @Override
        public MyMarker[] newArray(int size) {
            return new MyMarker[size];
        }
    };

    public MyLatLng getPosition() {
        return position;
    }

    public void setPosition(MyLatLng position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(this.position, flags);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(author);
        dest.writeString(uid);
        dest.writeString(imageId);
    }


    @Override
    public String toString() {
        return "Key: "+id+" - Latitude: "+position.getLatitude()+" Longitude: "+position.getLongitude();
    }
}
