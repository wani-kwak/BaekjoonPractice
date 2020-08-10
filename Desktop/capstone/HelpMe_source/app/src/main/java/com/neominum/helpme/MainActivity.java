package com.neominum.helpme;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private AppCompatButton m_acbtnToggle = null;

	private NotificationManager m_notificationManager = null;

	private class LocationListener implements android.location.LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			try {
				final Geocoder geocoder = new Geocoder(MainActivity.this);
				final List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

				if (addresses.size() > 0) {
					((AppCompatTextView) findViewById(R.id.activity_main_actv_location)).setText(addresses.get(0).getAddressLine(0) + "\n(" + location.getLatitude() + ", " + location.getLongitude() + ")");
				} else {
					((AppCompatTextView) findViewById(R.id.activity_main_actv_location)).setText("(" + location.getLatitude() + ", " + location.getLongitude() + ")");
				}
			} catch (Exception e) {
				((AppCompatTextView) findViewById(R.id.activity_main_actv_location)).setText("(" + location.getLatitude() + ", " + location.getLongitude() + ")");
				e.printStackTrace();
			}
		}

		@Override
		public void onStatusChanged(String s, int i, Bundle bundle) {

		}

		@Override
		public void onProviderEnabled(String s) {

		}

		@Override
		public void onProviderDisabled(String s) {

		}
	}
	private LocationListener[] f_listeners = new LocationListener[] {
		new LocationListener(),
		new LocationListener()
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().hide();

		final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(this, "위치 접근 권한이 꺼져있습니다", Toast.LENGTH_SHORT).show();
		} else {
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					0,
					0,
					f_listeners[0]);
			}

			if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER,
					0,
					0,
					f_listeners[1]);
			}
		}

		m_acbtnToggle = (AppCompatButton) findViewById(R.id.activity_main_acbtn_toggle);

		m_notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			final StatusBarNotification[] notifications = m_notificationManager.getActiveNotifications();

			for (final StatusBarNotification notification : notifications) {
				if (notification.getId() == 0 && notification.getTag() != null && notification.getTag().equals("HELP_ME_SHORT_CUT")) {
					m_acbtnToggle.setTag(true);
					m_acbtnToggle.setText("바로가기 끄기");
					return;
				}
			}

			m_acbtnToggle.setTag(false);
			m_acbtnToggle.setText("바로가기 켜기");
		} else {
			final Intent notificationIntent = new Intent(this, MainActivity.class);
			final PendingIntent test = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_NO_CREATE);

			if (test != null) {
				m_acbtnToggle.setTag(true);
				m_acbtnToggle.setText("바로가기 끄기");
			} else {
				m_acbtnToggle.setTag(false);
				m_acbtnToggle.setText("바로가기 켜기");
			}
		}
	}

	public void onRequest(View view) {

		final SharedPreferences sharedPreferences = getSharedPreferences("information", Context.MODE_PRIVATE);

		if (sharedPreferences.contains("personalMap")) {

			final String json = sharedPreferences.getString("personalMap", null);

			if (json != null) {
				final HashMap<String, String> personalMap = new Gson().fromJson(json, new TypeToken<HashMap<String, String>>() { }.getType());

				new AlertDialog.Builder(this)
					.setMessage("119, 긴급연락처 " + personalMap.get("phone") + "(" + personalMap.get("relationship") + ")에게 문자가 전송됩니다.\n 119 허위신고는 과태료 200만원의 벌금이 부과됩니다. 주의하세요!")
					.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startService(new Intent(MainActivity.this, SMSIntentService.class));
						}
					})
					.setNegativeButton("취소", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					})
					.show();
			} else {
				Toast.makeText(this, "개인정보가 삭제 돼었습니다. 앱을 초기화 해주세요", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "개인정보가 삭제 돼었습니다. 앱을 초기화 해주세요", Toast.LENGTH_SHORT).show();
		}
	}

	public void onModify(View view) {
		final Intent inputIntent = new Intent(this, InputActivity.class);
		inputIntent.putExtra("isModification", true);

		startActivity(inputIntent);
	}

	public void onToggle(View view) {
		if ((boolean) view.getTag()) {
			m_acbtnToggle.setTag(false);
			m_acbtnToggle.setText("바로가기 켜기");
			m_notificationManager.cancel("HELP_ME_SHORT_CUT", 0);
		} else {
			m_acbtnToggle.setTag(true);
			m_acbtnToggle.setText("바로가기 끄기");

			final Notification.Builder builder;
			final NotificationChannel channel;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				channel = new NotificationChannel("HELP_ME_SHORT_CUT_CHANNEL_ID", "HELP_ME_SHORT_CUT_CHANNEL_NAME", android.app.NotificationManager.IMPORTANCE_DEFAULT);
				channel.setDescription("channel description");
				channel.enableLights(true);
				channel.setLightColor(Color.GREEN);
				channel.enableVibration(true);
				channel.setVibrationPattern(new long[]{100, 200, 100, 200});
				channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
				notificationManager.createNotificationChannel(channel);

				builder = new Notification.Builder(this, "HELP_ME_SHORT_CUT_CHANNEL_ID");
			} else {
				channel = null;
				builder = new Notification.Builder(this);;
			}

			final Intent smsIntent = new Intent(getApplicationContext(), SMSIntentService.class);
			final PendingIntent smsPendingIntent = PendingIntent.getService(this, 0, smsIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			final Notification notification = builder
				.setOngoing(true)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setContentTitle("사이렌을 눌러 즉시 응급요청")
				.setContentText("119와 설정된 긴급연락처로 문자전송됩니다")
				.setColor(Color.parseColor("#FF0000"))
				.setSmallIcon(R.drawable.ic_siren)
	//			.addAction(R.drawable.ic_call, "Call Action", callPendingIntent)
				.addAction(R.drawable.ic_siren, "SMS Action", smsPendingIntent)
				.setStyle(new Notification.MediaStyle()
					.setShowActionsInCompactView(0/*, 1*/))
				.setPriority(Notification.PRIORITY_MAX)
				.build();

			m_notificationManager.notify("HELP_ME_SHORT_CUT", 0, notification);
		}
	}
}
