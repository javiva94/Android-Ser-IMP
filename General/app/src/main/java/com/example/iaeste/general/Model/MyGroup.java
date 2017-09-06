package com.example.iaeste.general.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by franc on 5/9/2017.
 */

public class MyGroup implements Parcelable {

    private String uid;
    private String displayName;
    private List<MyUser> members;
    private String owner_uid;

    public MyGroup (){

    }

    public MyGroup (String displayName, List<MyUser> members, String owner_uid){
        this.displayName = displayName;
        this.members = members;
        this.owner_uid = owner_uid;
    }

    protected MyGroup(Parcel in) {
        uid = in.readString();
        displayName = in.readString();
        members = in.createTypedArrayList(MyUser.CREATOR);
        owner_uid = in.readString();
    }

    public String getUid() {
        return uid;
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

    public void setUid(String uid) {
        this.uid = uid;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(displayName);
        dest.writeTypedList(members);
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
