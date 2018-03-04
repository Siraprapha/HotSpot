package dev.S.ink.hotspot;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Siraprapha on 3/4/2018.
 */


public interface JsonService{
    @GET("{url}")
    Call<JsonData> getJson(@Path("url") String url);

}
