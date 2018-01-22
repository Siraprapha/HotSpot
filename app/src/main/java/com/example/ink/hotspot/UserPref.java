package com.example.ink.hotspot;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public void sendToServer(){
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
                params.put("token_device", "");
                params.put("token_auth", getFacebookUserInfo(FB_ACCESS_TOKEN));
                params.put("role", "0");
                return params;
            }
        };
        queue.add(postRequest);
    }

}
