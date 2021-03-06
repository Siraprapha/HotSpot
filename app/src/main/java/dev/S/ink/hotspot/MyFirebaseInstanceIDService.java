package dev.S.ink.hotspot;
//CHECK TOKEN EXPIRED ?



import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by die_t on 12/20/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Toast.makeText(getApplicationContext(),refreshedToken,Toast.LENGTH_LONG).show();
        sendRegistrationToServer(refreshedToken);
        //userpref.saveDeviceToken(refreshedToken);
    }

    private void sendRegistrationToServer(final String token) {
        // TODO: Implement this method to send token to your app server.


        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());  // this = context
        final String url = "http://tatam.esy.es/usersystem/user_token.php";
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
                            //JSONObject datares = o.getJSONObject(Integer.toString(1));
                            String status = (String) o.get("status");

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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("post", "puttoken");
                params.put("method", "add");
                params.put("token_device", token);
                return params;
            }
        };
        queue.add(postRequest);
    }

}
