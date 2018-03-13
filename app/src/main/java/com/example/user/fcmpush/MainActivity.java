package com.example.user.fcmpush;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";

	public static final String PUSH_APP = "appName";
	public static final String PUSH_TITLE = "title";
	public static final String PUSH_SENDTYPE = "sendType";
	public static final String PUSH_TEXT = "text";
	public static final String PUSH_DATA = "data";

	public static final String PUSH_REQ_LOGIN = "requireLoginPage"; //로그인 필요한 페이지인지 flag

	public static final String SENDTYPE_HYBRID = "hybrid"; // 덴플 2.0

	TextView textView;
	TextView textView2;

	private FirebaseRemoteConfig remoteConfig;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = (TextView) findViewById(R.id.textView);
		textView2 = (TextView) findViewById(R.id.textView2);
		Button btn = (Button) findViewById(R.id.btnRefresh);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fetchConfig();
			}
		});

		remoteConfig = FirebaseRemoteConfig.getInstance();
		FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(true).build();

		Map<String, Object> map = new HashMap<>();
		map.put("friendly_msg_length", 100L);

		remoteConfig.setConfigSettings(settings); //
		remoteConfig.setDefaults(map); //기본값 설정

		fetchConfig();

		FirebaseMessaging.getInstance().subscribeToTopic("news");
		String token = FirebaseInstanceId.getInstance().getToken();

		Log.d(TAG, "Device token: " + token);


		if (getIntent().getExtras() != null) {
			if(getIntent().getExtras().getString("STRING") != null) {
				String data = getIntent().getExtras().getString("STRING");
				textView.setText(data);
			} else if(getIntent().getExtras().getString("DATA") != null) {
				String result = getIntent().getStringExtra("DATA");
				textView2.setText(result+" 으로 평가 완료");
			}

		}

	}


	public void fetchConfig() {
		/*앱 캐싱설정*/
		long cacheExpiration = 3600;

		if(remoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
			cacheExpiration = 0;
		}

		/*원격 구성 서버에서 값을 가져온다.*/
		remoteConfig.fetch(cacheExpiration).addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				remoteConfig.activateFetched();  //가져온 값을 앱에 적용
				applyRetrievedLengthLimit();
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				Log.w(TAG, "Error fetching config: " + e.getMessage());
				applyRetrievedLengthLimit();
			}
		});

	}


	private void applyRetrievedLengthLimit() {
		Long friendly_msg_length = remoteConfig.getLong("friendly_msg_length");
		textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(friendly_msg_length.intValue())});
		//textView.setText(remoteConfig.getString("main_text"));

		if(BuildConfig.VERSION_CODE <= remoteConfig.getLong("update_version"))
			showUpdateDialog();

		Log.d(TAG, "FML is : "+friendly_msg_length);


	}

	//업데이트 다이어로그
	public static final int FORCE_UPDATE = 1;  // 강제 업데이트
	public static final int SOFT_UPDATE = 2;  //  선택 업데이트
	public void showUpdateDialog() {

		String updateStr = remoteConfig.getString("update_content");
		String updateBtn = remoteConfig.getString("update_P_button");
		String cancelBtn = remoteConfig.getString("update_N_button");


		final Dialog dialog = createDefaultDialog(this,
				null,
				updateStr,
				updateBtn,
				cancelBtn,
				null);

		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public static Dialog createDefaultDialog(Context context,
											 String title,
											 String message,
											 String positive,
											 String negative,
											 final DlgListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if(!TextUtils.isEmpty(title)) {
			builder.setTitle(title);
		}

		if(!TextUtils.isEmpty(message)){
			builder.setMessage(message);
		}

		if( !TextUtils.isEmpty(positive)) {
			builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if( listener != null ) {
						listener.onClickDialogButton(Dialog.BUTTON_POSITIVE, which);
					}
				}
			});
		}

		if( !TextUtils.isEmpty(negative)) {
			builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if( listener != null ) {
						listener.onClickDialogButton(Dialog.BUTTON_NEGATIVE, which);
					}
				}
			});
		}

		Dialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}
	public interface DlgListener {

		/**
		 * Dialog button click listener
		 *
		 * @param button button type, see Dialog.BUTTON_*
		 * @param which if exist, which item clicked (ex. list, picker ...)
		 */
		public void onClickDialogButton(int button, int which);

	}

}
