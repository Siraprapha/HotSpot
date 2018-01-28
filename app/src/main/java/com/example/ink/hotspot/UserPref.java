package com.example.ink.hotspot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by die_t on 1/19/2018.
 */

public class UserPref {

    Context context;

    public final String PREFS_NAME = "MyPrefsFile";
    private final String TAG = "UserPref";
    private final String FB_ID = "fb_id";
    private final String FB_FIRST_NAME = "fb_first_name";
    private final String FB_NAME = "fb_name";
    private final String FB_EMAIL = "fb_email";
    private final String FB_ACCESS_TOKEN = "fb_access_token";
    private final String DEVICE_TOKEN = "device_token";
    private final String REGIS_NAME = "register_name";
    private final String REGIS_EMAIL = "register_email";
    private final String REGIS_PASSWORD = "register_password";
    private final String LOGIN_NAME = "login_name";
    private final String LOGIN_EMAIL = "login_email";
    private final String LOGIN_PASSWORD = "login_password";
    private final String RESPONSE_STATUS = "response_status";
    // fb_id fb_first_name fb_name fb_email currLocation

    // Constructor
    public UserPref(Context context) {
        this.context = context;
    }


    public String getDeviceToken() {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        Log.e(TAG, "getTokenDevice: "+prefs);
        return prefs.getString(DEVICE_TOKEN, null);
    }
    public void saveDeviceToken(String token) {
        if(token==null) {Log.e(TAG, "saveDeviceToken: ");}
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(DEVICE_TOKEN, token);
        editor.apply(); // This line is IMPORTANT !!!
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

    //Facebook login
    public void saveFacebookUserInfo(String id, String first_name, String name, String email){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(FB_ID, id);
        editor.putString(FB_FIRST_NAME, first_name);
        editor.putString(FB_NAME, name);
        editor.putString(FB_EMAIL, email);
        editor.apply(); // This line is IMPORTANT !!!
        Log.e(TAG, "Shared Name : "+prefs.getString(FB_FIRST_NAME,null)+"\nEmail : "+email);
    }
    public String getFacebookUserInfo(String str){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Log.e(TAG, "Name : "+prefs.getString(str,null)+"\nEmail : "+prefs.getString("fb_email",null));
        return prefs.getString(str,null);
    }
    public void sendFacebookUserInfoToServer(){
        RequestQueue queue = Volley.newRequestQueue(context);  // this = context
        final String url = "http://tatam.esy.es/usersystem/user_request.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject= null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray arr = jsonObject.getJSONArray("response");
                            JSONObject o = arr.getJSONObject(0);
                            JSONObject datares = o.getJSONObject(Integer.toString(1));
                            String status = (String) datares.get("status");
                            //Json(url,jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("post", "signup");
                params.put("method", "add");
                params.put("username", getFacebookUserInfo(FB_NAME));
                params.put("email", getFacebookUserInfo(FB_EMAIL));
                params.put("password", "");
                params.put("token_device", getDeviceToken());
                params.put("token_auth", getFacebookUserInfo(FB_ACCESS_TOKEN));
                params.put("role", "0");
                return params;
            }
        };
        queue.add(postRequest);
    }

    //Register
    private void saveRegisterUserInfo(String username,String password, String email){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REGIS_NAME, username);
        editor.putString(REGIS_PASSWORD, password);
        editor.putString(REGIS_EMAIL, email);
        editor.apply(); // This line is IMPORTANT !!!
        Log.e(TAG, "Shared Name : "+prefs.getString(REGIS_NAME,null)+"\nEmail : "+
                prefs.getString(REGIS_EMAIL,null)+"\nPassword : "+prefs.getString(REGIS_PASSWORD,null));
        sendRegisterUserInfoToServer();
    }
    public void sendRegisterUserInfoToServer(){
        RequestQueue queue = Volley.newRequestQueue(context);  // this = context
        final String url = "http://tatam.esy.es/usersystem/user_request.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject= null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray arr = jsonObject.getJSONArray("response");
                            JSONObject o = arr.getJSONObject(0);
                            JSONObject datares = o.getJSONObject(Integer.toString(1));
                            String status = (String) datares.get("status");
                            //Json(url,jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("post", "signup");
                params.put("method", "add");
                params.put("username", getFacebookUserInfo(REGIS_NAME));
                params.put("email", getFacebookUserInfo(REGIS_EMAIL));
                params.put("password", getFacebookUserInfo(REGIS_PASSWORD));
                params.put("token_device", getDeviceToken());
                params.put("token_auth", "");
                params.put("role", "0");
                return params;
            }
        };
        queue.add(postRequest);
    }

    //Login
    public void saveLoginUserInfo(String username,String password){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LOGIN_NAME, username);
        editor.putString(LOGIN_PASSWORD, password);
        editor.apply(); // This line is IMPORTANT !!!
        Log.e(TAG, "Shared Username : "+prefs.getString(LOGIN_NAME,null)+"\nPassword : "+prefs.getString(LOGIN_PASSWORD,null));
        sendLoginUserInfoToServer();
    }
    public void sendLoginUserInfoToServer(){
        RequestQueue queue = Volley.newRequestQueue(context);  // this = context
        final String url = "http://tatam.esy.es/usersystem/user_request.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG,"onResponse"+ response);
                        JSONObject jsonObject= null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray arr = jsonObject.getJSONArray("response");
                            JSONObject o = arr.getJSONObject(0);
                            JSONObject datares = o.getJSONObject(Integer.toString(1));
                            String status = (String) datares.get("status");
                            saveResponseStatus(status);
                            Log.d(TAG, "onResponse: status"+status);
                            //Json(url,jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //clearUserInfo();
                        // error
                        Log.e(TAG, error.getLocalizedMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("post", "signin");
                params.put("method", "request");
                params.put("username", getUserInfo(LOGIN_NAME));
                params.put("password", getUserInfo(LOGIN_PASSWORD));
                return params;
            }
        };
        queue.add(postRequest);
        Log.e(TAG, "sendLoginUserInfoToServer: finish");
    }

    public String getUserInfo(String str){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Log.e(TAG, "getUserInfo : "+str+" "+prefs.getString(str,null));
        return prefs.getString(str,null);
    }
    public void clearUserInfo(){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // This line is IMPORTANT !!!
        Log.e(TAG, "clearUserInfo: "+prefs.getString(LOGIN_NAME,null)+prefs.getString(LOGIN_PASSWORD,null));
    }

    private void saveResponseStatus(String str){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(RESPONSE_STATUS, str);
        editor.apply(); // This line is IMPORTANT !!!
        Log.e(TAG, "Shared Status : "+prefs.getString(RESPONSE_STATUS,null));
    }
    public String getResponseStatus(){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Log.e(TAG, "getResponseStatus : "+prefs.getString(RESPONSE_STATUS,null));
        return prefs.getString(RESPONSE_STATUS,null);
    }


}
