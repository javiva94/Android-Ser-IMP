package com.example.iaeste.general.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by franc on 31/8/2017.
 */

@IgnoreExtraProperties
public class MyUser implements Parcelable{

    private String uid;
    private String displayName;
    private String email;
    private String providerId;
    private List<String> groups = new ArrayList<>();
    private String role;

    public MyUser(){

    }

    public MyUser(String uid, String displayName){
        this.uid = uid;
        this.displayName = displayName;
    }

    protected MyUser(Parcel in) {
        uid = in.readString();
        displayName = in.readString();
        email = in.readString();
        providerId = in.readString();
        groups = in.createStringArrayList();
        role = in.readString();
    }

    public static final Creator<MyUser> CREATOR = new Creator<MyUser>() {
        @Override
        public MyUser createFromParcel(Parcel in) {
            return new MyUser(in);
        }

        @Override
        public MyUser[] newArray(int size) {
            return new MyUser[size];
        }
    };

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("displayName", displayName);
        result.put("email", email);
        result.put("providerId", providerId);
        result.put("groups", groups);
        result.put("role", role);

        return result;
    }

    public String getUid() {
        return uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getProviderId() {
        return providerId;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(displayName);
        dest.writeString(email);
        dest.writeString(providerId);
        dest.writeStringList(groups);
        dest.writeString(role);
    }
}
