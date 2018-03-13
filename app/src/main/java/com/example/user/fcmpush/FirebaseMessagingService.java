package com.example.user.fcmpush;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static com.example.user.fcmpush.SendNotification.sendPushNotification;

/**
 * Created by user on 2017-04-06.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    private static final String TAG = "FirebaseMegService";

    public void onMessageReceived (RemoteMessage remoteMessage) {
        Log.d(TAG, "Received remoteMessage: " + remoteMessage);
        //sendNotification(remoteMessage.getData().get("message"));
        //remoteMessage.getNotification().notify();
        if(remoteMessage.getData().size() > 0 ) {
            Bundle bundle = new Bundle();
            StringBuffer data = new StringBuffer();
            for(Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
                data.append(entry.getKey() +" : " + entry.getValue() + "\n");
            }
            sendPushNotification(this, bundle, data.toString());
        }
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //MainActivity 위쪽의 스택을 모두 제거
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT); //Flag on shot -> 일회용

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("FCM Push")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, notificationBuilder.build());
    }
}
