package dev.S.ink.hotspot;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Siraprapha on 3/4/2018.
 */

public class NetworkConnectionManager {
    public NetworkConnectionManager() {

    }

    public void callServer(final OnNetworkCallbackListener listener,String url){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://tatam.esy.es/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonService js = retrofit.create(JsonService.class);
        Call call = js.getJson(url);
        call.enqueue(new Callback<List<JsonData>>() {
            @Override
            public void onResponse(Call<List<JsonData>> call, Response<List<JsonData>> response) {
                List<JsonData> jsonData = response.body();

                if (jsonData == null) {
                    //404 or the response cannot be converted to User.
                    ResponseBody responseBody = response.errorBody();
                    if (responseBody != null) {
                        listener.onBodyError(responseBody);
                    } else {
                        listener.onBodyErrorIsNull();
                    }
                } else {
                    //200
                    listener.onResponse(jsonData);
                }
            }
            @Override
            public void onFailure(Call<List<JsonData>> call, Throwable t) {
                listener.onFailure(t);
            }
        });

    }
}
