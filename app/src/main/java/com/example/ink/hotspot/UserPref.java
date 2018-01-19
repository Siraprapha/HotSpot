package com.example.ink.hotspot;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by die_t on 1/19/2018.
 */

public class UserPref {

    Context context;

    public static final String PREFS_NAME = "MyPrefsFile";
    private static final String TAG = "UserPref";

    // fb_id fb_first_name fb_name fb_email currLocation

    // Constructor
    public UserPref(Context context) {
        this.context = context;
    }

    public void saveAccessToken(String token) {
        if(token==null) {Log.e(TAG, "saveAccessToken: ");}
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fb_access_token", token);
        editor.apply(); // This line is IMPORTANT !!!
    }


    public String getToken() {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        Log.e(TAG, "getToken: "+prefs);
        return prefs.getString("fb_access_token", null);
    }

    public void clearToken() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // This line is IMPORTANT !!!
    }

    public void saveFacebookUserInfo(String id, String first_name, String name, String email){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fb_id", id);
        editor.putString("fb_first_name", first_name);
        editor.putString("fb_name", name);
        editor.putString("fb_email", email);
        editor.apply(); // This line is IMPORTANT !!!
        Log.e(TAG, "Shared Name : "+prefs.getString("fb_first_name",null)+"\nEmail : "+email);
    }

    public String getFacebookUserInfo(String str){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Log.e(TAG, "Name : "+prefs.getString(str,null)+"\nEmail : "+prefs.getString("fb_email",null));
        return prefs.getString(str,null);
    }
}
