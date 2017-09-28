package com.example.iaeste.general.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Task implements Parcelable {

    @Exclude
    private String key;

  //  private List<MapObject> mapObjects = new ArrayList<>();
    private List<MyMarker> myMarkerList = new ArrayList<>();
    private List<MyPolyline> polylineList = new ArrayList<>();
    private List<MyPolygon> polygonList = new ArrayList<>();
    private String title;
    private String description;
    private String owner_uid;
    private List<MyGroup> groupList;
  //  private List<String> readUsersPermission = new ArrayList<String>();
  //  private List<String> writeUsersPermission = new ArrayList<String>();


    public Task(){
        // Default constructor required for calls to DataSnapshot.getValue(MyUser.class)
    }

    protected Task(Parcel in) {
        key = in.readString();
        owner_uid = in.readString();
        title = in.readString();
        description = in.readString();
        in.readList(myMarkerList, getClass().getClassLoader());
        in.readList(polylineList, getClass().getClassLoader());
        in.readList(polygonList, getClass().getClassLoader());
        in.readList(groupList, getClass().getClassLoader());
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
        result.put("key", key);
        result.put("title", title);
        result.put("description", description);
        result.put("owner_uid", owner_uid);
        result.put("myMarkerList", myMarkerList);
        result.put("polylineList", polylineList);
        result.put("polygonList", polygonList);
        result.put("groupList", groupList);
       // result.put("readPermission", readUsersPermission);
       // result.put("writePermission", writeUsersPermission);

        return result;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setOwner_uid(String owner_uid) {
        this.owner_uid = owner_uid;
    }

    public String getOwner_uid() {
        return owner_uid;
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

    public List<MyMarker> getMyMarkerList() {
        return myMarkerList;
    }

    public void setMyMarkerList(List<MyMarker> myMarkerList) {
        this.myMarkerList = myMarkerList;
    }

    public List<MyPolyline> getPolylineList() {
        return polylineList;
    }

    public void setPolylineList(List<MyPolyline> polylineList) {
        this.polylineList = polylineList;
    }

    public List<MyPolygon> getPolygonList() {
        return polygonList;
    }

    public void setPolygonList(List<MyPolygon> polygonList) {
        this.polygonList = polygonList;
    }

 /*   public List<String> getReadUsersPermission() {
        return readUsersPermission;
    }

    public void setReadUsersPermission(List<String> readUsersPermission) {
        this.readUsersPermission = readUsersPermission;
    }

    public List<String> getWriteUsersPermission() {
        return writeUsersPermission;
    }

    public void setWriteUsersPermission(List<String> writeUsersPermission) {
        this.writeUsersPermission = writeUsersPermission;
    }
    */

    public List<MyGroup> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<MyGroup> groupList) {
        this.groupList = groupList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(owner_uid);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeList(myMarkerList);
        dest.writeList(polylineList);
        dest.writeList(polygonList);
        dest.writeList(groupList);

    }

    public MyMarker getPointById(String id){
        boolean find = false;
        int i=0;
        while(!find){
            if (myMarkerList.get(i).getId().equals(id)) {
                return myMarkerList.get(i);
            }i++;
        }
        return null;
    }

    public MyPolyline getPolylineById(String id){
        boolean find = false;
        int i=0;
        while(!find){
            if (polylineList.get(i).getId().equals(id)) {
                return polylineList.get(i);
            }i++;
        }
        return null;
    }

    public MyPolygon getPolygonById(String id){
        boolean find = false;
        int i=0;
        while(!find){
            if (polygonList.get(i).getId().equals(id)) {
                return polygonList.get(i);
            }i++;
        }
        return null;
    }

}
