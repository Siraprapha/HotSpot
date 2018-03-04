package dev.S.ink.hotspot;

/**
 * Created by Siraprapha on 3/4/2018.
 */

public class JsonData {
    private String name;
    private String latitude;
    private String longitude;
    private String lat;
    private String lng;
    private String date;
    private String time;
    private String satellite;
    private String pm10;

    public void setName(String n){
        name = n;
    }
    public void setLatitude(String n){
        latitude = n;
    }
    public void setLongitude(String n){
        longitude = n;
    }
    public void setlat(String n){
        lat = n;
    }
    public void setlng(String n){
        lng = n;
    }
    public void setDate(String n){
        date = n;
    }
    public void setTime(String n){
        time = n;
    }
    public void setSatellite(String n){
        satellite = n;
    }
    public void setPM10(String n){
        pm10 = n;
    }

    public String getName(){
        return name;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){
        return longitude;
    }
    public String getLat(){
        return lat;
    }
    public String getLng(){
        return lng;
    }
    public String getDate(){
        return date;
    }
    public String getTime(){
        return time;
    }
    public String getSatellite(){
        return satellite;
    }
    public String getPM10(){
        return pm10;
    }

}
