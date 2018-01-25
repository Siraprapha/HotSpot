package com.example.ink.hotspot;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by Siraprapha on 1/25/2018.
 */

public class Register extends Fragment{
    private UserPref userpref;

    private LinearLayout register_block;
    private EditText email;
    private EditText username;
    private EditText password;
    private EditText confirm_password;
    private Button cancel_register;
    private Button confirm_register;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);

    }

}
