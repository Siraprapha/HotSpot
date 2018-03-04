package dev.S.ink.hotspot;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by Siraprapha on 3/4/2018.
 */

public interface OnNetworkCallbackListener {
    public void onResponse(List<JsonData> list_jsonData);
    public void onBodyError(ResponseBody responseBodyError);
    public void onBodyErrorIsNull();
    public void onFailure(Throwable t);
}
