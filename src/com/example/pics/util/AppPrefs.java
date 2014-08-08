package com.example.pics.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {
    private static final String USER_INFO = "user_info";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private String mUserName = "user_name_prefs";
    private String mUserId = "user_id_prefs";
    private String mParentUserId = "parent_user_id_prefs";
    private String mParentMissionId = "parent_mission_id_prefs";

    public AppPrefs(Context context){
        this.appSharedPrefs = context.getSharedPreferences(USER_INFO, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public String getUserId() {
        return appSharedPrefs.getString(mUserId, "unknown");
    }

    public void setUserId(String userId) {
        prefsEditor.putString(mUserId, userId).commit();
    }
    
    public String getUserName() {
        return appSharedPrefs.getString(mUserName, "unknown");
    }

    public void setUserName(String userName) {
        prefsEditor.putString(mUserName, userName).commit();
    }
    
    public String getParentUserId() {
        return appSharedPrefs.getString(mParentUserId, "unknown");
    }

    public void setParentUserId(String parentUserId) {
        prefsEditor.putString(mParentUserId, parentUserId).commit();
    }
    
    public String getParentMissionId() {
        return appSharedPrefs.getString(mParentMissionId, "unknown");
    }

    public void setParentId(String parentMissionId) {
        prefsEditor.putString(mParentMissionId, parentMissionId).commit();
    }    
}