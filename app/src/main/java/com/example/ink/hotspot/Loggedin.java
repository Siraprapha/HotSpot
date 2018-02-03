package com.example.ink.hotspot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Siraprapha on 1/28/2018.
 */

public class Loggedin extends Fragment {

    Context context;
    Activity activity;

    UserPref userPref;

    TextView username_textview;
    Button logout_button;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_loggedin, container, false);

        username_textview = rootview.findViewById(R.id.username_textview);
        logout_button = rootview.findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPref.clearUserInfo();
                activity.getFragmentManager().popBackStack();
                Fragment fragment = Login.newInstance();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.map, fragment).addToBackStack(null).commit();
            }
        });

        return rootview;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        activity = (Activity) context;
        super.onAttach(context);
    }
}
