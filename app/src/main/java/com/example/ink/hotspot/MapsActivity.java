package com.example.ink.hotspot;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.kml.KmlLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener{

    public DrawerLayout mDrawer;
    public Toolbar toolbar;
    public NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;

    private GoogleMap mMap;

    private KmlLayer layer;

    private final Handler handler = new Handler();

    private static final String TAG = "MapsActivity";

    //Current Location
    CurrentLocation currLocate ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_maps);

        //Map
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            currLocate.checkLocationPermission();
        }

        setDrawer();

        if(savedInstanceState == null){
            CreateMap();
        }
        else {

        }
        String value;
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            value = bundle.getString("casenoti");
            if(value != null){
                switch (value){
                    case "fireupdate":{
                        Log.e("push noti test", "fire update" );
                        break;
                    }
                    case "userrequest":{
                        break;
                    }
                    default:break;
                }
            }
        }

    }
    //toolbar toggle
    public void setDrawer(){
        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Find our drawer view
        mDrawer = findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        // Find our drawer view
        nvDrawer = findViewById(R.id.nvView);
        // Inflate the header view at runtime
        //View headerLayout = nvDrawer.inflateHeaderView(R.layout.nav_header);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
    }

    //Toggle
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //Select Item
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }
    public static final String EXTRA_MESSAGE = "Inkie1234";
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.home:
                fragmentClass = MapsFragment.class;
                mDrawer.openDrawer(GravityCompat.START);

                break;
            case R.id.ffmc:
                //retrieveFileFromUrl();
                showKML(mMap,0);
                //fragmentClass = CallNoti.class;
                break;
            case R.id.fwi:
                showKML(mMap,1);
                break;
            case R.id.st_forest:
                //
                caseJson(1);
                break;
            case R.id.st_wilds:
                //
                caseJson(2);
                break;
            case R.id.call:
                fragmentClass = CallFromUser.class;
                break;
            case R.id.about:
                //fragmentClass = wildfire_notify.class;
                break;
            default:
                fragmentClass = null;
        }

        // Highlight the selected item has been done by NavigationView
        //menuItem.setChecked(true);
        if(fragmentClass!= null){
            try {
                NewFragment(fragmentClass);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        // Set action bar title
        TextView toolbar = findViewById(R.id.toolbar_text);
        toolbar.setText(menuItem.getTitle());

        // Close the navigation drawer
        mDrawer.closeDrawers();
    }
    public void NewFragment(Class fragmentClass) throws IllegalAccessException, InstantiationException {
        Fragment fragment = (Fragment) fragmentClass.newInstance();
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.map, fragment).commit();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
        //TextView tv = (TextView)findViewById(R.id.txt_view);
        //delete all fragment except first one

        /*switch (v.getId()) {
            case R.id.subscribeButton://sub
                FirebaseMessaging.getInstance().subscribeToTopic("droiddev/news");
                Log.d(TAG, "SubscribeToTopic");
                Toast.makeText(MapsActivity.this, "SubscribeToTopic", Toast.LENGTH_SHORT).show();
                break;
            case R.id.unsubscribeButton://unsub
                FirebaseMessaging.getInstance().unsubscribeFromTopic("droiddev/news");
                Log.d(TAG, "UnsubscribeFromTopic");
                Toast.makeText(MapsActivity.this, "UnsubscribeFromTopic", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logTokenButton://show token
                String token = FirebaseInstanceId.getInstance().getToken();
                Log.d(TAG, "Token : " + token);
                Toast.makeText(MapsActivity.this, "Token : " + token, Toast.LENGTH_SHORT).show();
                break;
        }*/
    }

    //Start Map
    public void CreateMap() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Inky","ACCESS_FINE_LOCATION");
                currLocate = new CurrentLocation(mMap);
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            //ShowDialog();
            Log.e("Inky","NO ACCESS_FINE_LOCATION");
            caseJson(0);
            currLocate = new CurrentLocation(mMap);
            mMap.setMyLocationEnabled(true);
            //handler.postDelayed(runnable, 10000);
        }
    }

    public final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            Toast.makeText(MapsActivity.this, "Refresh ", Toast.LENGTH_SHORT).show();
            //handler.postDelayed(runnable,10000);
        }
    };

    //Marker From JSON
    public void caseJson(int key){
        String url;
        switch (key){
            case 0: url = "http://tatam.esy.es/api.php?key=map";CallJsonHotSpot(mMap,url);break;    //fire hot spot
            case 1: url = "http://tatam.esy.es/api.php?key=station&sub=forest";CallJson(mMap,url);break;    //forest station
            case 2: {
                url = "http://tatam.esy.es/api.php?key=station&sub=wild";
                //url = "http://tatam.esy.es/getair4thai.php";
                CallJson(mMap,url);
                break;    //wild station
            }
            default: url = "";
        }

    }
    public void CallJson(GoogleMap googleMap,String url){
        mMap = googleMap;
        //mMap.clear();
        if (!url.equals("")){
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("test",response.toString());

                            try {
                                JSONArray arr = response.getJSONArray("posts");
                                double lat;
                                double lng;
                                for(int i=0;i<arr.length();i++){
                                    JSONObject o = arr.getJSONObject(i);
                                    JSONObject datares = o.getJSONObject(Integer.toString(i+1));
                                    lat =  Double.parseDouble((String)datares.get("latitude"));
                                    lng =  Double.parseDouble((String)datares.get("longitude"));
//                                    lat =  (double)datares.get("latitude");
//                                    lng =  (double)datares.get("longitude");
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)));
                                    //String name = datares.get("name").toString();
                                }
                                //move camera to the last lat,lng marker
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(currLocate.getCurrLatLng()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("Json", "Finish mark json");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.e("Json", "Error on"+error);
                        }
                    });

// Access the RequestQueue through your singleton class.
            MySingleton.getInstance(MapsActivity.this).addToRequestQueue(jsObjRequest);
        }
        else{
            Log.e("Json", "URL not found.");
            Toast.makeText(this,"URL Not found.",Toast.LENGTH_SHORT).show();
        }


    }
    public void CallJsonHotSpot(GoogleMap googleMap,String url){
        mMap = googleMap;
        //mMap.clear();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("test",response.toString());

                        try {
                            JSONArray arr = response.getJSONArray("posts");
                            double lat;
                            double lng;
                            for(int i=0;i<arr.length();i++){
                                JSONObject o = arr.getJSONObject(i);
                                JSONObject datares = o.getJSONObject(Integer.toString(i+1));
                                lat =  Double.parseDouble((String)datares.get("latitude"));
                                lng =  Double.parseDouble((String)datares.get("longitude"));
//                                    lat =  (double)datares.get("latitude");
//                                    lng =  (double)datares.get("longitude");
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.flame)));
                                //String name = datares.get("name").toString();
                            }
                            //move camera to the last lat,lng marker
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currLocate.getCurrLatLng()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("Json", "Finish mark json");
                        Toast.makeText(getApplicationContext(),"Finish mark json",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e("Json", "Error on"+error);
                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(jsObjRequest);

    }

    //KML
    private void showKML(GoogleMap googleMap,int key){
        mMap = googleMap;
        //mMap.clear();
        if(layer != null){
            layer.removeLayerFromMap();
        }
        try {
            Log.e("KML", "showKML: ");
            Toast.makeText(this,"start show KML",Toast.LENGTH_SHORT).show();
            if(key==0){
                //FFMC
                //KmlLayer layer = new KmlLayer(mMap, kmlInputStream, getApplicationContext());
                layer = new KmlLayer(mMap, R.raw.ffmc, getApplicationContext());
                layer.addLayerToMap();
            }
            else{
                //FWI
                layer = new KmlLayer(mMap, R.raw.fwi, getApplicationContext());
                layer.addLayerToMap();
            }
            Log.e("KML", "showKML: already");
            Toast.makeText(this,"finish show KML",Toast.LENGTH_SHORT).show();

            //movecamera

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Dialog
    private void ShowDialog(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(MapsActivity.this);
        builder.setMessage("Please check your internet connection.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getApplicationContext(),
                        "...", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
            }
        });
        builder.show();
    }

    //Notification

    public void showNotification(View view) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this,CHANNEL_ID)
                        .setSmallIcon(R.drawable.image)
                        .setContentTitle("Fire Alarm")
                        .setContentText("พบพิกัดไฟป่าเพิ่ม");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MapsActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MapsActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager.notify(001, mBuilder.build());

    }


}