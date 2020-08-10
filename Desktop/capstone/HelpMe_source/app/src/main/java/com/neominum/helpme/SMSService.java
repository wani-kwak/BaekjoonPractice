package com.neominum.helpme;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SMSService extends Service {

	public SMSService() {
		Log.d("NEOMINUM", "SMSService");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void sendSMS(final Double latitude, final Double longitude) {
		try {
			final SmsManager smsManager = SmsManager.getDefault();

			final SharedPreferences sharedPreferences = getSharedPreferences("information", Context.MODE_PRIVATE);

			if (sharedPreferences.contains("personalMap")) {

				final String json = sharedPreferences.getString("personalMap", null);

				if (json != null) {
					final HashMap<String, String> personalMap = new Gson().fromJson(json, new TypeToken<HashMap<String, String>>() {}.getType());

					final StringBuffer textBuffer = new StringBuffer();

					textBuffer.append(
						"이름: " + personalMap.get("name") + "\n" +
						"나이: " + personalMap.get("age") + "\n" +
						"생년월일: " + personalMap.get("year") + " " + personalMap.get("month") + " " + personalMap.get("day") + "\n" +
						"혈액형: " + personalMap.get("blood") + "(" + personalMap.get("rh") + ")" + "\n" +
						"주소: " + personalMap.get("address") + " " + personalMap.get("detail") + "\n" +
						"비상연락처: " + personalMap.get("phone") + "(" + personalMap.get("relationship") + ")" + "\n" +
						"지병: " + personalMap.get("disease") + "\n" +
						"부작용약: " + personalMap.get("drug") + "\n" +
						"선호병원: " + personalMap.get("hospital") + "\n" +
						"주치의: " + personalMap.get("doctor") + "\n" +
						"남길말: " + personalMap.get("ment"));

					if (latitude != null && longitude != null) {

						final Geocoder geocoder = new Geocoder(this);
						final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

						if (addresses.size() > 0) {
							textBuffer.append(
								"\n" +
									"현위치: " + "(" + latitude + ", " + longitude + ")" + "\n" +
									addresses.get(0).getAddressLine(0));
						}
					}

					final ArrayList<String> divided = smsManager.divideMessage(textBuffer.toString());

					if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
						return;
					} else{
						final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						final String selfPhoneNumber = telephonyManager.getLine1Number();

						smsManager.sendMultipartTextMessage(
							selfPhoneNumber,
							selfPhoneNumber,
							divided,
							null,
							null);

						Thread.sleep(5000);

						smsManager.sendMultipartTextMessage(
							personalMap.get("phone"),
							selfPhoneNumber,
							divided,
							null,
							null);
					}
				} else {
					Toast.makeText(this, "사용자 정보가 훼손돼었습니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "사용자 정보가 훼손돼었습니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private LocationManager m_locationManager = null;
	private boolean m_alreadyCatchLocation = false;

	private class LocationListener implements android.location.LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (!m_alreadyCatchLocation) {
				m_alreadyCatchLocation = true;
				sendSMS(location.getLatitude(), location.getLongitude());
				stopSelf();
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
	public void onCreate() {
//		super.onCreate();

		m_locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			sendSMS(null, null);
		} else {
			if (m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				m_locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					0,
					0,
					f_listeners[0]);
			}

			if (m_locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				m_locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER,
					0,
					0,
					f_listeners[1]);
			}
		}

		return START_STICKY;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();

		try {
			m_locationManager.removeUpdates(f_listeners[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			m_locationManager.removeUpdates(f_listeners[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
