package com.example.user.fcmpush;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by user on 2017-04-06.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService{
    private static final String TAG = "MyFirebaseIDService";

    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        sendInstanceID(token);
    }

    private void sendInstanceID(String token){
        Intent intent = new Intent();
        intent.setAction("com.example.xnote.INSTANCE_ID_CM_RECEIVE");
        intent.putExtra("RECEIVE_TYPE","RECEIVE_FCM");
        intent.putExtra("TOKEN_ID", token);
        sendBroadcast(intent);
    }

}
