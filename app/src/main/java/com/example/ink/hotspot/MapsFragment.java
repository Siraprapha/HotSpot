package com.example.ink.hotspot;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import com.google.maps.android.data.kml.KmlLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    public GoogleMap mMap;

    private CurrentLocation currLocate;

    private KmlLayer layer;

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.fragment_map_container);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState); mMapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                Log.e("Inky","ACCESS_FINE_LOCATION");
                currLocate = new CurrentLocation(mMap);
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            //ShowDialog();
            Log.e("Inky","NO ACCESS_FINE_LOCATION fragment");
            mMap.setMyLocationEnabled(true);
            currLocate = new CurrentLocation(mMap);
            String url = "http://tatam.esy.es/api.php?key=map";
            CallJsonHotSpot(mMap, url);
            //handler.postDelayed(runnable, 10000);
        }
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
            MySingleton.getInstance(getContext()).addToRequestQueue(jsObjRequest);
        }
        else{
            Log.e("Json", "URL not found.");
            Toast.makeText(getContext(),"URL Not found.",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(),"Finish mark json",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e("Json", "Error on"+error);
                    }
                });

// Access the RequestQueue through your singleton class.
        MySingleton.getInstance(getContext()).addToRequestQueue(jsObjRequest);

    }

    //KML
    public void showKML(GoogleMap googleMap,int key){
        mMap = googleMap;
        //mMap.clear();
        if(layer != null){
            layer.removeLayerFromMap();
            Log.e("showKML", "showKML: layer is not null");
        }
        try {
            Log.e("KML", "showKML: ");
            Toast.makeText(getContext(),"start show KML",Toast.LENGTH_SHORT).show();
            //if(getContext()!=null) {Log.e("Context", "showKML: Context is notnull"+getContext());}
            if(key==0){
                //FFMC
                //KmlLayer layer = new KmlLayer(mMap, kmlInputStream, getApplicationContext());
                layer = new KmlLayer(mMap, R.raw.ffmc, getContext());
                //layer.addLayerToMap();
            }
            else{
                //FWI
                layer = new KmlLayer(mMap, R.raw.fwi, getContext());
                layer.addLayerToMap();
            }
            //Toast.makeText(getContext(),"ไม่พบข้อมูล"+layer.getContainers().toString(),Toast.LENGTH_SHORT).show();
            if(layer==null)Log.e("Context", "showKML: Context is notnull"+getContext());
            Log.e("KML", "showKML: already");
            Toast.makeText(getContext(),"finish show KML",Toast.LENGTH_SHORT).show();

            //movecamera

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
