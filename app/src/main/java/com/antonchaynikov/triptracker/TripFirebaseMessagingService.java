package com.antonchaynikov.triptracker;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class TripFirebaseMessagingService extends FirebaseMessagingService {

    private final static String TAG = TripFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        new Handler(Looper.getMainLooper()).post( () -> {
            Toast.makeText(getApplicationContext(), remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG).show();
        });
    }

}
