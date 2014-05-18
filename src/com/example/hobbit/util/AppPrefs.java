package com.example.hobbit.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class AppPrefs {
    private static final String USER_INFO = "user_info";
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private String user_name = "user_name_prefs";
    private String user_id = "user_id_prefs";

    public AppPrefs(Context context){
        this.appSharedPrefs = context.getSharedPreferences(USER_INFO, Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    public String getUser_id() {
        return appSharedPrefs.getString(user_id, "unknown");
    }

    public void setUser_id(String _user_id) {
        prefsEditor.putString(user_id, _user_id).commit();
    }
    public String getUser_name() {
        return appSharedPrefs.getString(user_name, "unknown");
    }

    public void setUser_name(String _user_name) {
        prefsEditor.putString(user_name, _user_name).commit();
    }
}