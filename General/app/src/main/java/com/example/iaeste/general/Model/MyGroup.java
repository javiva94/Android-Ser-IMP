package com.example.iaeste.general.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by franc on 5/9/2017.
 */

public class MyGroup implements Parcelable {

    private String id;
    private String displayName;
    private List<MyUser> members;
    private List<Task> tasks = new ArrayList<>();
    private String owner_uid;

    public MyGroup (){

    }

    public MyGroup (String displayName, List<MyUser> members, String owner_uid){
        this.displayName = displayName;
        this.members = members;
        this.owner_uid = owner_uid;
    }

    protected MyGroup(Parcel in) {
        id = in.readString();
        displayName = in.readString();
        members = in.createTypedArrayList(MyUser.CREATOR);
        tasks = in.createTypedArrayList(Task.CREATOR);
        owner_uid = in.readString();
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("displayName", displayName);
        result.put("members", members);
        result.put("tasks", tasks);
        result.put("owner_uid", owner_uid);

        return result;
    }

    public String getId() {
        return id;
    }

    public String getOwner_uid() {
        return owner_uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<MyUser> getMembers() {
        return members;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOwner_uid(String owner_uid) {
        this.owner_uid = owner_uid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setMembers(List<MyUser> members) {
        this.members = members;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(displayName);
        dest.writeTypedList(members);
        dest.writeTypedList(tasks);
        dest.writeString(owner_uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyGroup> CREATOR = new Creator<MyGroup>() {
        @Override
        public MyGroup createFromParcel(Parcel in) {
            return new MyGroup(in);
        }

        @Override
        public MyGroup[] newArray(int size) {
            return new MyGroup[size];
        }
    };
}
