package com.example.ink.hotspot;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.data.kml.KmlLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int DEFAULT_ZOOM = 5;

    private static final String TAG = "MapsFragment";

    private Activity activity;

    private Context context;

    public GoogleMap mMap;

    private CurrentLocation currLocate;
    LocationManager locationManager;
    String provider;
    LatLng myPosition;

    private KmlLayer layer;

    boolean mLocationPermissionGranted;
    FusedLocationProviderClient mFusedLocationProviderClient;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;
    Location mLastKnownLocation;
    LatLng mDefaultLocation = new LatLng(13.738938, 100.527688);

    public static Fragment newInstance() {
        MapsFragment m = new MapsFragment();
        return m;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_map_container, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);

        Toast.makeText(context, "MapsFragment is on stack", Toast.LENGTH_LONG).show();
        return rootview;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.e("Inky", "NO ACCESS_FINE_LOCATION fragment");
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        /*
        locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition.Builder cameraPosition = new CameraPosition.Builder();
        cameraPosition.target(center);
        cameraPosition.zoom(DEFAULT_ZOOM);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()));*/

        //currLocate = new CurrentLocation(mMap,activity);
        //String url = "http://tatam.esy.es/api.php?key=map";
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,DEFAULT_ZOOM));
        String url = "http://tatam.esy.es/test/querytestmodule.php?key=maprealtime";
        CallJsonHotSpot(mMap, url);
        //handler.postDelayed(runnable, 10000);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public GoogleMap getmMap(){
        return mMap;
    }

    //Marker From JSON
    public void caseJson(int key){
        String url;
        switch (key){
            case 0: {    //forest station
                url = "http://tatam.esy.es/api.php?key=station&sub=forest";
                CallJsonStation(mMap,url);
                break;
            }
            case 1: {    //wild station
                url = "http://tatam.esy.es/api.php?key=station&sub=wild";
                //url = "http://tatam.esy.es/getair4thai.php";
                CallJsonStation(mMap,url);
                break;
            }
            default: url = "";
        }

    }
    public void CallJsonStation(GoogleMap googleMap,String url){
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
                                    String name = (String) datares.get("name");
                                    lat =  Double.parseDouble((String)datares.get("latitude"));
                                    lng =  Double.parseDouble((String)datares.get("longitude"));
//                                    lat =  (double)datares.get("latitude");
//                                    lng =  (double)datares.get("longitude");
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)));

                                }
                                //move camera to the last lat,lng marker
                                //mMap.moveCamera(CameraUpdateFactory.newLatLng(currLocate.getCurrLatLng()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e("Json", "Finish mark json");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.e("Json", "Error on"+error.getLocalizedMessage());
                        }
                    });

// Access the RequestQueue through your singleton class.
            MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);
        }
        else{
            Log.e("Json", "URL not found.");
            Toast.makeText(context,"URL Not found.",Toast.LENGTH_SHORT).show();
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
                                Log.e(TAG, "onResponse: lat "+lat+"   long "+lng+"\n");
                                mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.flame)));
                                //String name = datares.get("name").toString();
                            }
                            //move camera to the last lat,lng marker
                            //mMap.moveCamera(CameraUpdateFactory.newLatLng(currLocate.getCurrLatLng()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("Json", "Finish mark json");
                        Toast.makeText(context,"Finish mark json",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e("Json", "Error on"+error);
                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsObjRequest);

    }

    //KML
    public void showKML(GoogleMap googleMap,int key,int day){
        mMap = googleMap;
        //mMap.clear();

        if(layer != null){
            Log.e(TAG, "showKML: layer "+layer.toString());
            layer.removeLayerFromMap();
            Log.e("showKML", "showKML: layer is not null");
            Log.e(TAG, "showKML: layer removed "+layer.toString());
        }
        try {
            Log.e("KML", "showKML: ");
            //Toast.makeText(context,"start show KML",Toast.LENGTH_SHORT).show();
            if(key==0){
                switch (day){
                    case 1:layer = new KmlLayer(mMap, R.raw.ffmc_past_2, context);break;
                    case 2:layer = new KmlLayer(mMap, R.raw.ffmc_past_1, context);break;
                    case 3:layer = new KmlLayer(mMap, R.raw.ffmc, context);break;
                    case 4:layer = new KmlLayer(mMap, R.raw.ffmc_future_1, context);break;
                    case 5:layer = new KmlLayer(mMap, R.raw.ffmc_future_2, context);break;
                    default:layer = new KmlLayer(mMap, R.raw.ffmc, context);break;
                }
                //FFMC
                //KmlLayer layer = new KmlLayer(mMap, kmlInputStream, getApplicationContext());
                //layer = new KmlLayer(mMap, R.raw.ffmc, context);
                layer.addLayerToMap();
            }
            else{
                switch (day){
                    case 1:layer = new KmlLayer(mMap, R.raw.fwi_past_2, context);break;
                    case 2:layer = new KmlLayer(mMap, R.raw.fwi_past_1, context);break;
                    case 3:layer = new KmlLayer(mMap, R.raw.fwi, context);break;
                    case 4:layer = new KmlLayer(mMap, R.raw.fwi_future_1, context);break;
                    case 5:layer = new KmlLayer(mMap, R.raw.fwi_future_2, context);break;
                    default:layer = new KmlLayer(mMap, R.raw.fwi, context);break;
                }
                //FWI
                //layer = new KmlLayer(mMap, R.raw.fwi, context);
                layer.addLayerToMap();
            }
            //Toast.makeText(getContext(),"ไม่พบข้อมูล"+layer.getContainers().toString(),Toast.LENGTH_SHORT).show();
            if(layer==null)Log.e("Context", "showKML: Context is notnull"+context);
            Log.e("KML", "showKML: already");
            Toast.makeText(context,"finish show KML",Toast.LENGTH_SHORT).show();
            //movecamera
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }
    public void removeLayer(){
        if(layer!=null){
            layer.removeLayerFromMap();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (Activity)context;
    }


}
