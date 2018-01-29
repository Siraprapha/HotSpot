package com.example.ink.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Siraprapha on 1/25/2018.
 */

public class Register extends Fragment{

    static final String TAG = "Register";

    private LinearLayout register_block;
    private EditText email,username,password,confirm_password;
    private Button cancel_register,confirm_register;

    private UserPref userPref;

    private Context context;
    private Activity activity;

    public static Fragment newInstance() {
        Register r = new Register();
        return r;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_register, container, false);

        userPref = new UserPref(context);

        email = rootview.findViewById(R.id.email);
        username = rootview.findViewById(R.id.username);
        password = rootview.findViewById(R.id.password);
        confirm_password = rootview.findViewById(R.id.confirm_password);
        cancel_register = rootview.findViewById(R.id.cancel_register);
        cancel_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getFragmentManager().popBackStackImmediate();
            }
        });
        confirm_register = rootview.findViewById(R.id.confirm_register);
        confirm_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = email.getText().toString().trim();
                String Username = username.getText().toString().trim();
                String Password = password.getText().toString().trim();
                String Confirm_password = confirm_password.getText().toString().trim();

                if(Validate(Username,Password,Confirm_password,Email)){
                    Log.d(TAG, "onClick: validate true"+Username+Password+Email);
                    userPref.saveRegisterUserInfo(Username,Password,Email);

                }
                else {
                    Log.d(TAG, "onClick: validate false");
                }
            }
        });

        Toast.makeText(context,"Login is on stack",Toast.LENGTH_LONG).show();
        return rootview;

    }

    private boolean Validate(String u,String p,String cp,String e){
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        //Pattern.compile(EMAIL_PATTERN).matcher(e).matches()
        if(e.equals("")){
            email.setError("อีเมลล์เว้นว่างไม่ได้");
            email.requestFocus();
            return false;
        }
        if(!(Patterns.EMAIL_ADDRESS.matcher(e).matches())){
            email.setError("อีเมลล์ไม่ถูกต้อง");
            email.requestFocus();
            return false;
        }
        if(u.equals("")){
            username.setError("ชื่อผู้ใช้เว้นว่างไม่ได้");
            username.requestFocus();
            return false;
        }
        if(u.length()<4 || u.length()>20){
            username.setError("ชื่อผู้ใช้ต้องมีอย่างน้อย 4 ตัวอักษร และไม่เกิน 20 ตัวอักษร ลองใหม่อีกครั้ง");
            username.requestFocus();
            return false;
        }
        if(p.equals("")){
            password.setError("รหัสผ่านเว้นว่างไม่ได้");
            password.requestFocus();
            return false;
        }
        if(p.length()<6||p.equals("")){
            password.setError("รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร ลองใหม่อีกครั้ง");
            password.requestFocus();
            return false;
        }
        if(cp.equals("")){
            confirm_password.setError("ช่องนี้เว้นว่างไม่ได้");
            confirm_password.requestFocus();
            return false;
        }
        if(!p.equals(cp)){
            confirm_password.setError("รหัสไม่ตรงกัน ลองใหม่อีกครั้ง");
            username.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (Activity)context;
    }

    private void ShowDialog(String message){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
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
