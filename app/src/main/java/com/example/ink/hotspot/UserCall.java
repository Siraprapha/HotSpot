package com.example.ink.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class UserCall extends Fragment {

    Activity activity;
    Context context;

    Button call_button;

    public static Fragment newInstance() {
        UserCall uc = new UserCall();
        return uc;
    }

    public UserCall() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_user_call, container, false);
        call_button = rootview.findViewById(R.id.call_button);
        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogPhoneCall();
            }
        });
        return rootview;
    }

    //Dialog
    private void ShowDialogPhoneCall(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setMessage("คุณต้องการโทรด่วน?");
        builder.setPositiveButton("โทร", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent phone_call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:1362"));
                startActivity(phone_call);
            }
        });
        builder.setNegativeButton("ไว้ก่อน", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
