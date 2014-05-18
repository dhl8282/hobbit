package com.example.hobbit.util;
import java.io.Serializable;
import java.util.HashSet;

import com.google.android.gms.maps.model.LatLng;

@SuppressWarnings("serial")
public class Mission implements Serializable{
    private String title, hint, picPath, userId;
    private double longitude, latitude;
    private LatLng mark;
    private User user;
    private Mission parent = null;
    private HashSet<Mission> children = null;

    public Mission() {

    }

    public Mission(String title, String hint, double longitude, double latitude) {
        super();
        this.title = title;
        this.hint = hint;
        this.longitude = longitude;
        this.latitude = latitude;
        this.mark = new LatLng(latitude, longitude);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Mission getParent() {
        return parent;
    }

    public void setParent(Mission parent) {
        this.parent = parent;
    }

    public HashSet<Mission> getChildren() {
        return children;
    }

    public void setChildren(HashSet<Mission> children) {
        this.children = children;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public LatLng getMark() {
        return mark;
    }

    public void setMark(LatLng mark) {
        this.mark = mark;
    }

}
