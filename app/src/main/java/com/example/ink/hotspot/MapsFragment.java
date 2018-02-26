package com.example.ink.hotspot;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.data.kml.KmlLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final int DEFAULT_ZOOM = 8;
    private static final int DEFAULT_ZOOM_2 = 7;
    private LatLng DEFAULT_LATLNG = new LatLng(18.128,99.54);
    private Marker CURRENT_MARKER;

    private static final String TAG = "MapsFragment";

    private static Activity activity;

    private Context context;

    public GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;

    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();

    private KmlLayer layer;

    //LatLng mDefaultLocation = new LatLng(13.738938, 100.527688);

    ArrayList<Marker> sat_terra = new ArrayList<>();
    ArrayList<Marker> sat_aqua = new ArrayList<>();
    ArrayList<Marker> sat_sumi = new ArrayList<>();

    ArrayList<Marker> markers_forest;
    ArrayList<Marker> markers_wild;
    ArrayList<Marker> markers_pm10;
    public static boolean isForestonMap = false, isWildonMap = false, isPM10onMap = false;

    boolean isLayeronMap = false;

    public static Fragment newInstance() {
        MapsFragment m = new MapsFragment();
        return m;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Registers BroadcastReceiver to track network connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        activity.registerReceiver(receiver, filter);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_map, container, false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_map_container, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

//        Toast.makeText(context, "MapsFragment is on stack", Toast.LENGTH_LONG).show();
        return rootview;
    }
    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregisters BroadcastReceiver when app is destroyed.
        if (receiver != null) {
            activity.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //createLocationRequest();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000);
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "onMapReady:new app version: " + Build.VERSION_CODES.M + " device version: " + android.os.Build.VERSION.SDK_INT);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    ){
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
//                mFusedLocationClient.getLastLocation()
//                        .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//                                // Got last known location. In some rare situations this can be null.
//                                if (location != null) {
//                                    // Logic to handle location object
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),
//                                            DEFAULT_ZOOM));
//                                }
//                            }
//                        });
            } else {
                Log.e(TAG, "onMapReady:>23 permission false");
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            Log.e(TAG, "onMapReady: old app version: " + Build.VERSION_CODES.M + " device version: " + android.os.Build.VERSION.SDK_INT);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
        //currLocate = new CurrentLocation(mMap,activity);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LATLNG, DEFAULT_ZOOM));

        //String url = "http://tatam.esy.es/test/api.php?key=maprealtime";
        //showKML();
//        String url = "http://tatam.esy.es/api.php?key=map";
//        CallJsonHotSpot(url);
//        if(isNetworkConn()){
//            Log.e(TAG, "onMapReady: has internet conn" );
//            String url = "http://tatam.esy.es/api.php?key=map";
//            CallJsonHotSpot(url);
//        }else {
//            Log.e(TAG, "onMapReady: no internet conn" );
//            //checkNetworkPermission();
//            showInternetAlertDialog();
//        }
/*
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });*/
        //handler.postDelayed(runnable, 10000);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.BLACK);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.i(TAG, "onLocationResult: "+locationResult.getLocations());
            for (Location location : locationResult.getLocations()) {
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
//                if (mCurrLocationMarker != null) {
//                    Log.e(TAG, "onLocationResult: mCurrLocationMarker = "+mCurrLocationMarker );
//                    mCurrLocationMarker.remove();
//                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                markerOptions.title("Current Position");
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//                //mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            //Log.i(TAG, "onLocationAvailability: "+locationAvailability.toString());
            if(!locationAvailability.isLocationAvailable()){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LATLNG, DEFAULT_ZOOM_2));
                //Log.i(TAG, "onLocationAvailability 2: "+locationAvailability.toString());
            }
        }
    };

    //Permission
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_NETWORK = 100;
    public void checkLocationPermission() {
        Log.e(TAG, "checkLocationPermission: ");
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );

            }
        }
    }
    public void checkNetworkPermission(){
        Log.e(TAG, "checkNetworkPermission: ");
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.INTERNET)) {
                Log.e(TAG, "checkNetworkPermission: show dialog.");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Network Permission Needed")
                        .setMessage("This app needs the Network permission, please accept to use Network functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.INTERNET},
                                        MY_PERMISSIONS_REQUEST_NETWORK );
                            }
                        });
                builder.create();
            } else {
                Log.e(TAG, "checkNetworkPermission: request permission" );
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_NETWORK );
            }
        }
    }
    public static boolean isNetworkConn(){
        ConnectivityManager connMgr = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();
        Log.d(TAG, "Wifi connected: " + isWifiConn);
        Log.d(TAG, "Mobile connected: " + isMobileConn);
        return isWifiConn || isMobileConn;
    }
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "permission granted", Toast.LENGTH_LONG).show();
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
//                        mFusedLocationClient.getLastLocation()
//                                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
//                                    @Override
//                                    public void onSuccess(Location location) {
//                                        // Got last known location. In some rare situations this can be null.
//                                        if (location != null) {
//                                            // Logic to handle location object
//                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),
//                                                    DEFAULT_ZOOM));
//                                            Log.e(TAG, "onRequestPermissionsResult: move camera" );
//                                        }
//                                    }
//                                });
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(context, "permission denied", Toast.LENGTH_LONG).show();
                    showGPSAlertDialog();
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LATLNG,DEFAULT_ZOOM));

                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_NETWORK: {
// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.INTERNET)
                            == PackageManager.PERMISSION_GRANTED){

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
        }
    }

    //check wifi
    //open wifi

    //JSON: station = forest, wild, pm10
    public void removeMarker(ArrayList<Marker> h) {
        Log.e(TAG, "removeMarker: " + h.size());
        Log.e(TAG, "removeMarker: " + h.toString());
        for (int i = 0; i < h.size(); i++) {
            Marker m = h.get(i);
            m.remove();
            //m.setVisible(false);
        }
        h.clear();
        Log.e(TAG, "removeMarker: finished " + h.size());
    }
    public void hideMarker(ArrayList<Marker> h) {
        for (int i = 0; i < h.size(); i++) {
            Marker m = h.get(i);
            m.setVisible(false);
        }
    }
    public void showMarker(ArrayList<Marker> h) {
        for (int i = 0; i < h.size(); i++) {
            Marker m = h.get(i);
            m.setVisible(true);
        }
    }
    public ArrayList<Marker> maxSizeArrayList(ArrayList<Marker> a, ArrayList<Marker> b, ArrayList<Marker> c) {
        if (a.size() > b.size()) {
            if (a.size() > c.size()) {
                return a;
            } else return c;
        } else {
            if (b.size() > c.size()) {
                return b;
            } else return c;
        }
    }
    //on click item
    public void onCallJson(int key) {
        if(!isNetworkConn()){
            showInternetAlertDialog();
            return;
        }
        switch (key) {
            case 0: {
                if (!isForestonMap) {
                    CallJsonForest();
                    isForestonMap = true;
                } else {
                    removeMarker(markers_forest);
                    isForestonMap = false;
                }
                break;
            }
            case 1: {
                if (!isWildonMap) {
                    CallJsonWild();
                    isWildonMap = true;
                } else {
                    removeMarker(markers_wild);
                    isWildonMap = false;
                }
                break;
            }
            case 2: {
                if (!isPM10onMap) {
                    CallJsonPM10();
                    isPM10onMap = true;
                } else {
                    removeMarker(markers_pm10);
                    isPM10onMap = false;
                }
                break;
            }
        }
    }
    //Marker From JSON: hot spot
    public void CallJsonHotSpot(String url) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("test", response);
                        try {
                            JSONObject job = new JSONObject(response);
                            JSONArray arr = job.getJSONArray("posts");
                            sat_terra = new ArrayList<>();
                            sat_aqua = new ArrayList<>();
                            sat_sumi = new ArrayList<>();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject o = arr.getJSONObject(i);
                                JSONObject datares = o.getJSONObject(Integer.toString(i + 1));
                                double lat = Double.parseDouble((String) datares.get("latitude"));
                                double lng = Double.parseDouble((String) datares.get("longitude"));
                                String sat = (String) datares.get("satellite");
                                String date = (String) datares.get("acq_date");
                                String time = (String) datares.get("acq_time");
                                Marker m;
                                switch (sat) {
                                    case "Terra":
                                        m = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(lat, lng))
                                                .title("พิกัดไฟป่า")
                                                .snippet("ดาวเทียม: " + sat + "\nวันและเวลา: " + date + " " + time + "\nตำแหน่ง: " + lat + ", " + lng)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.flame_blue))
                                                .visible(false));
                                        sat_terra.add(m);
                                        break;
                                    case "Aqua":
                                        m = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(lat, lng))
                                                .title("พิกัดไฟป่า")
                                                .snippet("ดาวเทียม: " + sat + "\nวันและเวลา: " + date + " " + time + "\nตำแหน่ง: " + lat + ", " + lng)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.flame16))
                                                .visible(false));
                                        sat_aqua.add(m);
                                        break;
                                    default:
                                        m = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(lat, lng))
                                                .title("พิกัดไฟป่า")
                                                .snippet("ดาวเทียม: " + sat + "\nวันและเวลา: " + date + " " + time + "\nตำแหน่ง: " + lat + ", " + lng)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.flame_green))
                                                .visible(false));
                                        sat_sumi.add(m);
                                        break;
                                }
                            }
                            ArrayList<Marker> t = maxSizeArrayList(sat_aqua, sat_sumi, sat_terra);
                            if (t == sat_aqua) {
                                showMarker(sat_aqua);
                            } else if (t == sat_sumi) {
                                showMarker(sat_sumi);
                            } else showMarker(sat_terra);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("Json", "Finish mark json");
//                        Toast.makeText(context,"Finish mark json",Toast.LENGTH_SHORT).show();
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
        );
// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(postRequest);
    }
    public void showSat(int key) {
        switch (key) {
            case 0: {
                if (!sat_terra.isEmpty()) {
                    if (sat_terra.get(0).isVisible()) {
                        hideMarker(sat_terra);
                    } else showMarker(sat_terra);
                } else Toast.makeText(context, "ไม่พบข้อมูล", Toast.LENGTH_SHORT).show();
                break;
            }
            case 1: {
                if (!sat_aqua.isEmpty()) {
                    if (sat_aqua.get(0).isVisible()) {
                        hideMarker(sat_aqua);
                    } else showMarker(sat_aqua);
                } else Toast.makeText(context, "ไม่พบข้อมูล", Toast.LENGTH_SHORT).show();
                break;
            }
            case 2: {
                if (!sat_sumi.isEmpty()) {
                    if (sat_sumi.get(0).isVisible()) {
                        hideMarker(sat_sumi);
                    } else showMarker(sat_sumi);
                } else Toast.makeText(context, "ไม่พบข้อมูล", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
    //call json : forest, wild, pm10
    public void CallJsonForest() {
        String url = "http://tatam.esy.es/api.php?key=station&sub=forest";
        if (!url.equals("")) {
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("test", response);
                            try {
                                JSONObject job = new JSONObject(response);
                                JSONArray arr = job.getJSONArray("posts");
                                Log.e(TAG, "rootJsonStation: array length" + arr.length());

                                markers_forest = new ArrayList<>();

                                for (int i = 0; i < arr.length(); i++) {

                                    JSONObject o = arr.getJSONObject(i);
                                    JSONObject datares = o.getJSONObject(Integer.toString(i + 1));

                                    MarkerOptions markerOptions ;
                                        markerOptions = new MarkerOptions()
                                                .position(new LatLng(Double.parseDouble((String) datares.get("latitude"))
                                                        , Double.parseDouble((String) datares.get("longitude"))))
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.security_home_forest))
                                                .title((String) datares.get("name"));

                                    Marker marker = mMap.addMarker(markerOptions);

                                    markers_forest.add(marker);
                                    Log.e(TAG, "addMarkersStation: " + markers_forest.size());
                                    Log.e(TAG, "addMarkersStation: " + Arrays.toString(markers_forest.toArray()));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("Json", "Finish mark json");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.e("Json", "Error on" + error.getLocalizedMessage());
                        }
                    }){
                @Override
                protected Response<String> parseNetworkResponse(
                        NetworkResponse response) {

                    String strUTF8 = null;
                    try {
                        strUTF8 = new String(response.data, "UTF-8");

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    }
                    return Response.success(strUTF8,
                            HttpHeaderParser.parseCacheHeaders(response));
                }
            };

// Access the RequestQueue through your singleton class.
            MySingleton.getInstance(context).addToRequestQueue(postRequest);
        } else {
            Log.e("Json", "URL not found.");
//            Toast.makeText(context,"URL Not found.",Toast.LENGTH_SHORT).show();
        }
    }
    public void CallJsonWild() {
        String url = "http://tatam.esy.es/api.php?key=station&sub=wild";
        if (!url.equals("")) {
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("test", response);
                            try {
                                JSONObject job = new JSONObject(response);
                                JSONArray arr = job.getJSONArray("posts");
                                Log.e(TAG, "rootJsonStation: array length" + arr.length());

                                markers_wild = new ArrayList<>();

                                for (int i = 0; i < arr.length(); i++) {

                                    JSONObject o = arr.getJSONObject(i);
                                    JSONObject datares = o.getJSONObject(Integer.toString(i + 1));

                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble((String) datares.get("latitude"))
                                                    , Double.parseDouble((String) datares.get("longitude"))))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.security_home_wild))
                                            .title((String) datares.get("name"));

                                    Marker marker = mMap.addMarker(markerOptions);

                                    markers_wild.add(marker);
                                    Log.e(TAG, "addMarkersStation: " + markers_wild.size());
                                    Log.e(TAG, "addMarkersStation: " + Arrays.toString(markers_wild.toArray()));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("Json", "Finish mark json");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.e("Json", "Error on" + error.getLocalizedMessage());
                        }
                    }){
                @Override
                protected Response<String> parseNetworkResponse(
                        NetworkResponse response) {
                    String strUTF8 = null;
                    try {
                        strUTF8 = new String(response.data, "UTF-8");

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    }
                    return Response.success(strUTF8,
                            HttpHeaderParser.parseCacheHeaders(response));
                }
            };
// Access the RequestQueue through your singleton class.
            MySingleton.getInstance(context).addToRequestQueue(postRequest);
        } else {
            Log.e("Json", "URL not found.");
//            Toast.makeText(context,"URL Not found.",Toast.LENGTH_SHORT).show();
        }
    }
    public void CallJsonPM10() {
        String url = "http://tatam.esy.es/getair4thai.php";
        if (!url.equals("")) {
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            Log.d("test", response.toString());
                            try {
                                //JSONObject job = new JSONObject(response);
                                JSONArray arr = response.getJSONArray("posts");
                                Log.e(TAG, "rootJsonStation: array length" + arr.length());

                                markers_pm10 = new ArrayList<>();

                                for (int i = 0; i < arr.length(); i++) {

                                    JSONObject datares = arr.getJSONObject(i);

                                    String nameTH = (String) datares.get("nameTH");
                                    String areaTH = (String) datares.get("areaTH");
                                    double lat = Double.parseDouble((String) datares.get("lat"));
                                    double lng = Double.parseDouble((String) datares.get("lng"));
                                    String date = (String) datares.get("date");
                                    String time = (String) datares.get("time");
                                    String pm10 = (String) datares.get("pm10");
                                    String unit = (String) datares.get("unit");

                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(new LatLng(lat, lng))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                                            .title("ค่าฝุ่นละอองในอากาศ PM10: " + pm10 + " " + unit)
                                            .draggable(true)
                                            .snippet("สถานี: " + nameTH + "\n " + areaTH + "\nวันที่: " + date + " เวลา: " + time);

                                    Marker marker = mMap.addMarker(markerOptions);

                                    markers_pm10.add(marker);

                                    Log.e(TAG, "addMarkersStation: " + markers_pm10.size());
                                    Log.e(TAG, "addMarkersStation: " + Arrays.toString(markers_pm10.toArray()));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("Json", "Finish mark json");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.e("Json", "Error on" + error.getLocalizedMessage());
                        }
                    });
// Access the RequestQueue through your singleton class.
            MySingleton.getInstance(context).addToRequestQueue(postRequest);
        } else {
            Log.e("Json", "URL not found.");
//            Toast.makeText(context,"URL Not found.",Toast.LENGTH_SHORT).show();
        }
    }

    //KML
    public void showKML(int key, int day) {
        //mMap.clear();
        if (layer != null) {
            Log.e(TAG, "showKML: layer " + layer.toString());
            layer.removeLayerFromMap();
            isLayeronMap = false;
            Log.e("showKML", "showKML: layer is not null");
            Log.e(TAG, "showKML: layer removed " + layer.toString());
        }
        try {
            Log.e("KML", "showKML: ");
            //Toast.makeText(context,"start show KML",Toast.LENGTH_SHORT).show();
            if (key == 0) {
                switch (day) {
                    case 1:
                        layer = new KmlLayer(mMap, R.raw.ffmc_0, context);
                        break;
                    case 2:
                        layer = new KmlLayer(mMap, R.raw.ffmc_1, context);
                        break;
                    case 3:
                        layer = new KmlLayer(mMap, R.raw.ffmc_2, context);
                        break;
                    case 4:
                        layer = new KmlLayer(mMap, R.raw.ffmc_3, context);
                        break;
                    case 5:
                        layer = new KmlLayer(mMap, R.raw.ffmc_4, context);
                        break;
                    case 6:
                        layer = new KmlLayer(mMap, R.raw.ffmc_5, context);
                        break;
                    default:
                        layer = new KmlLayer(mMap, R.raw.ffmc_0, context);
                        break;
                }
                //FFMC
                //KmlLayer layer = new KmlLayer(mMap, kmlInputStream, getApplicationContext());
                //layer = new KmlLayer(mMap, R.raw.ffmc, context);

                layer.addLayerToMap();
                isLayeronMap = true;
            } else {
                switch (day) {
                    case 1:
                        layer = new KmlLayer(mMap, R.raw.fwi_0, context);
                        break;
                    case 2:
                        layer = new KmlLayer(mMap, R.raw.fwi_1, context);
                        break;
                    case 3:
                        layer = new KmlLayer(mMap, R.raw.fwi_2, context);
                        break;
                    case 4:
                        layer = new KmlLayer(mMap, R.raw.fwi_3, context);
                        break;
                    case 5:
                        layer = new KmlLayer(mMap, R.raw.fwi_4, context);
                        break;
                    case 6:
                        layer = new KmlLayer(mMap, R.raw.fwi_5, context);
                        break;
                    default:
                        layer = new KmlLayer(mMap, R.raw.fwi_0, context);
                        break;
                }
                //FWI
                //layer = new KmlLayer(mMap, R.raw.fwi, context);
                layer.addLayerToMap();
                isLayeronMap = true;
            }
            //Toast.makeText(getContext(),"ไม่พบข้อมูล"+layer.getContainers().toString(),Toast.LENGTH_SHORT).show();
            if (layer == null) Log.e("Context", "showKML: Context is notnull" + context);
            Log.e("KML", "showKML: already");
//            Toast.makeText(context,"finish show KML",Toast.LENGTH_SHORT).show();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }
    public void removeLayer() {
        if (layer != null) {
            layer.removeLayerFromMap();
        }
    }

    //Dialog Handle
    public static void showInternetAlertDialog(){
        Log.e(TAG, "showInternetAlertDialog: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("คุณไม่ได้เชื่อมต่ออินเตอร์เน็ต")
                .setMessage("กรุณาเชื่อมต่ออินเตอร์เน็ต")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
        Log.e(TAG, "showInternetAlertDialog: created");
    }
    public void showGPSAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("คุณไม่ได้เชื่อมต่อgps")
                .setMessage("กรุณาเปิดgps")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        dialogInterface.dismiss();
                    }
                });
        builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (Activity) context;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Checks the user prefs and the network connection. Based on the result, decides whether
            // to refresh the display or keep the current display.
            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
            if (isNetworkConn()) {
                // If device has its Wi-Fi connection, sets refreshDisplay
                // to true. This causes the display to be refreshed when the user
                // returns to the app.
                Toast.makeText(context, "เชื่อมต่ออินเตอร์เน็ตแล้ว", Toast.LENGTH_SHORT).show();
                String url = "http://tatam.esy.es/api.php?key=map";
                CallJsonHotSpot(url);
            } else {
                Toast.makeText(context, "ไม่มีการเชื่อมต่ออินเตอร์เน็ต", Toast.LENGTH_SHORT).show();
                showInternetAlertDialog();
            }
        }
    }
}
