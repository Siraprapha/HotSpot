package com.example.ink.hotspot;
//RECEIVE DATA FROM SERVER

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by die_t on 12/20/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Message";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Log.e("notification",notification.getTitle());
        Log.e("notification",notification.getBody());

    }

}
