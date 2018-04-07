package dev.S.ink.hotspot;

import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

/**
 * Created by Siraprapha on 4/7/2018.
 */

public class Retrofit2Services {
    final private static String BASE_URL = "http://tatam.esy.es";

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface jsonService{
    }

    public static class FireStat{
        int year;
        int[] fire_spot;
        public int getYear(){
            return year;
        }
        public int[] getFire_spot(){
            return fire_spot;
        }
    }

}
