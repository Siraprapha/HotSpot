package com.example.ink.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Login extends Fragment {

    private LoginListener loginListener;

    private static final String TAG = "Login";
    private static final String SET_NAME_IF_NOT_LOGIN = "ลงชื่อเข้าใช้";

    private EditText fill_username,fill_password;
    private Button button_login,button_register;
    LoginButton loginButton;

    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;

    private boolean is_login = false;

    private List<String> permissions;

    private String[] user_data = null;

    private UserPref userPref;
    private Context context;
    private Activity activity;

    public static Fragment newInstance() {
        Login l = new Login();
        return l;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getContext());

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                //old = null means still logged in
                //current = null means still loged out
                if(currentAccessToken!=null){
                    fetchUserInfo(currentAccessToken);
                }else{
                    fetchUserInfo(oldAccessToken);
                }
            }
        };
        callbackManager = CallbackManager.Factory.create();*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_login, container, false);

        //username = rootview.findViewById(R.id.user_name);
        fill_username = rootview.findViewById(R.id.fill_username);
        fill_password = rootview.findViewById(R.id.fill_password);
        button_login = rootview.findViewById(R.id.button_login);
        button_register = rootview.findViewById(R.id.button_register);

        userPref = new UserPref(context);
        //onClicklogin
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = fill_username.getText().toString().trim();
                String password = fill_password.getText().toString().trim();
                if(Objects.equals(username, "") || Objects.equals(password, "")){
                    ShowDialog("กรุณาใส่ชื่อผู้ใช้หรือรหัส");
                }
                else{
                    userPref.saveLoginUserInfo(username,password);
                    String status = userPref.getResponseStatus();
                    Log.e(TAG, "onClick: "+status);
                    /*switch(status){
                        case "200": {
                            loginListener.onLoginSuccess(userPref.getUserInfo("login_name"));
                            Toast.makeText(getApplicationContext(),"login success",Toast.LENGTH_LONG).show();
                        }
                        case "invalid query row < 1 or >1": {
                            ShowDialog("ชื่อผู้ใช้หรือรหัสไม่ถูกต้อง กรุณาสมัครสมาชิก");
                            userPref.clearUserInfo();
                        }
                        case "\"error query":{
                            ShowDialog("กรุณาลองใหม่อีกครั้ง");
                        }
                        default:
                            Log.e(TAG, "onClick: button_login ResponseStatus == null");
                    }*/
                }
                //validate
            }
        });
        //onClickregister
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = Register.newInstance();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.map, fragment).commit();
            }
        });
        //onClickfacebook
        /*
        permissions = Arrays.asList("public_profile","email");
        loginButton = rootview.findViewById(R.id.login_button_fb);
        FacebookLoginButton();*/

        Toast.makeText(context,"Login is on stack",Toast.LENGTH_LONG).show();
        return rootview;
    }
    /*
    private void FacebookLoginButton(){

        loginButton.setReadPermissions(permissions);
        // If using in a fragment
        loginButton.setFragment(this);
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                is_login = true;
                Log.e(TAG, "onSuccess: "+loginResult.getAccessToken().getToken());
                accessToken = loginResult.getAccessToken();
                fetchUserInfo(accessToken);

                String userToken = loginResult.getAccessToken().getToken();
                userPref.saveAccessToken(userToken);

                Toast.makeText(context,"Welcome "+userPref.getFacebookUserInfo("fb_first_name"),Toast.LENGTH_LONG).show();
                //loginListener.onLoginSuccess(true);
            }
            @Override
            public void onCancel() {
                // App code
                is_login = false;
                Toast.makeText(context,"Cancel Login ",Toast.LENGTH_LONG).show();
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
                is_login = false;
                Toast.makeText(context,"Login Error ",Toast.LENGTH_LONG).show();
                Log.e(TAG, "onError: "+exception.getMessage());
                Log.e(TAG, "onError: "+exception.getCause());
                deleteAccessToken();
            }
        });
        //onClick loginButton
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: "+userPref.getToken());
                LoginManager.getInstance().logInWithReadPermissions(activity, permissions);
            }
        });
    }*/
    //request json from facebook
    /*
    private void fetchUserInfo(AccessToken ac) {
        if (ac != null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    ac, new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject me, GraphResponse response) {
                            Bundle bu = getFacebookData(me);
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,name,email");
            request.setParameters(parameters);
            GraphRequest.executeBatchAsync(request);
        } else {
            Toast.makeText(context,"accessToken is null",Toast.LENGTH_LONG).show();
        }
    }*/
    //json to string
    /*
    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();

        try {
            String id = object.getString("id");
            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("name"))
                bundle.putString("name", object.getString("name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            userPref.saveFacebookUserInfo(object.getString("id"),object.getString("first_name"),
                                        object.getString("name"),object.getString("email"));
            //setUserName();
        } catch (Exception e) {
            Log.d(TAG, "BUNDLE Exception : "+e.toString());
        }

        return bundle;
    }*/
/*
    private void setUserName(){
        String name = userPref.getFacebookUserInfo("fb_first_name");
        if(name!=null){
            username.setText(name);
            Log.e(TAG, "setUserName: "+userPref.getFacebookUserInfo("fb_first_name"));
        }else{
            name = "ลงชื่อเข้าใช้";
            username.setText(name);
            Log.e(TAG, "setUserName: "+userPref.getFacebookUserInfo("fb_first_name"));
        }
    }
*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (Activity)context;
        try {
            loginListener = (MapsActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement LoginListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //accessTokenTracker.stopTracking();
        //userPref.sendToServer();
    }

    public interface LoginListener{
        void onLoginSuccess(String username);
    }
    private void deleteAccessToken() {
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    //User logged out
                    userPref.clearToken();
                    LoginManager.getInstance().logOut();
                }
            }
        };
    }

    private void ShowDialog(String message){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getContext(),
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

}
