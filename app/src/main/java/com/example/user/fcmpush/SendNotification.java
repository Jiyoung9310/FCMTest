package com.example.user.fcmpush;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static android.app.Notification.FLAG_AUTO_CANCEL;

/**
 * Created by user on 2017-12-20.
 */

public class SendNotification {
	public static final String PUSH_APP = "appName";
	public static final String PUSH_TITLE = "title";
	public static final String PUSH_SENDTYPE = "sendType";
	public static final String PUSH_TEXT = "text";
	public static final String PUSH_DATA = "data";

	public static final String PUSH_REQ_LOGIN = "requireLoginPage"; //로그인 필요한 페이지인지 flag

	private static final String PUSH_LANDING_TYPE = "landingType";
	private static final String PUSH_URL_TARGET = "targetUrl";
	private static final String PUSH_URL_IMAGE = "imageUrl";


	public static final String SENDTYPE_HYBRID = "hybrid"; // 덴플 2.0

	public static void sendPushNotification(Context mContext, Bundle bundle, String data) {

		String jsonData = "";
		String title = "";
		String text = "";
		String sendType = "";
		boolean reqLogin;
		try {
			title = URLDecoder.decode(bundle.getString(PUSH_TITLE), "UTF-8");
			text = URLDecoder.decode(bundle.getString(PUSH_TEXT), "UTF-8");
			jsonData = URLDecoder.decode(bundle.getString(PUSH_DATA), "UTF-8");
			sendType = URLDecoder.decode(bundle.getString(PUSH_SENDTYPE), "UTF-8");
			reqLogin = Boolean.parseBoolean(URLDecoder.decode(bundle.getString(PUSH_REQ_LOGIN), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			title = "DENPLE";
			text = "Notice from DenPLE";
			sendType = SENDTYPE_HYBRID;
			reqLogin = false;
			jsonData = "";
			e.printStackTrace();
		} catch (NullPointerException e) {
			title = "DENPLE";
			text = "Notice from DenPLE";
			sendType = SENDTYPE_HYBRID;
			reqLogin = false;
			jsonData = "";
			e.printStackTrace();
		}

		Intent intent = new Intent(mContext, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra("BUNDLE", bundle);
		intent.putExtra("STRING", data);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);


		NotificationManagerCompat notiMgr = NotificationManagerCompat.from(mContext);
		notiMgr.notify(0, createPushNotification(mContext, pendingIntent, title, text));

		NotificationCompat.EXTRA_MEDIA_SESSION;

	}

	private static Notification createPushNotification(Context mContext, PendingIntent pIntent, String title , String message) {
		Intent intenta = new Intent(mContext, MainActivity.class);
		intenta.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intenta.putExtra("DATA", "매우만족");
		PendingIntent intent1 = PendingIntent.getActivity(mContext, 0, intenta, 0);

		Intent intentb = new Intent(mContext, MainActivity.class);
		intentb.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intentb.putExtra("DATA", "약간만족");
		PendingIntent intent2 = PendingIntent.getActivity(mContext, 0, intentb, 1);

		Intent intentc = new Intent(mContext, MainActivity.class);
		intentc.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intentc.putExtra("DATA", "보통");
		PendingIntent intent3 = PendingIntent.getActivity(mContext, 0, intentc, 2);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(title)
				.setContentText(message)
				.setAutoCancel(true)
				.setTicker(message)
				.setPriority(Notification.PRIORITY_HIGH)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setDefaults(Notification.DEFAULT_ALL)
				.setContentIntent(pIntent)
				.addAction(R.drawable.ic_dialog_survey_blue_normal, "매우만족", intent1) // #0
				.addAction(R.drawable.ic_dialog_survey_yellow_normal, "약간만족", intent2)  // #1
				.addAction(R.drawable.ic_dialog_survey_red_normal, "보통", intent3)     // #2
				.setStyle(new NotificationCompat.BigTextStyle().bigText(message));


		return builder.build();
	}


}
