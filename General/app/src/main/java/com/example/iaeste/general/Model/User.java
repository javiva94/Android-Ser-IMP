package com.example.iaeste.general.Model;

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
public class User {

    private String uid;
    private String displayName;
    private String email;
    private String providerId;
    private List<String> groups = new ArrayList<>();

    public User(){

    }

    public  User(String uid, String displayName){
        this.uid = uid;
        this.displayName = displayName;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("displayName", displayName);
        result.put("email", email);
        result.put("providerId", providerId);
        result.put("groups", groups);

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
}
