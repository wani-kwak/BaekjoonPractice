package com.neominum.helpme;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

public class SMSIntentService extends IntentService {

	public SMSIntentService() {
		super("SMSIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final Intent smsServiceIntent = new Intent(getApplicationContext(), SMSService.class);
		startService(smsServiceIntent);
	}
}
