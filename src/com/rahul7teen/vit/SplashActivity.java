package com.rahul7teen.vit;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SplashActivity extends SherlockActivity {
	Thread launch;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	// please enter your sender id
	String SENDER_ID = "475554169730";

	static final String TAG = "GCMDemo";
	GoogleCloudMessaging gcm;
	Context context;
	String regid;

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	class RegisterBackground extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				regid = gcm.register(SENDER_ID);
				msg = "Device registered, registration ID=" + regid;
				Log.d("111", msg);
				sendRegistrationIdToBackend();

				// Persist the regID - no need to register again.
				storeRegistrationId(context, regid);
			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
			}
			return msg;
		}

		@Override
		protected void onPostExecute(String msg) {

		}

		private void sendRegistrationIdToBackend() {
			// Your implementation here.

			String url = "http://www.vitacademics.in/images/androidapp/getdevice.php";
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("regid", regid));
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(params));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				httpClient.execute(httpPost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void storeRegistrationId(Context context, String regId) {
			final SharedPreferences prefs = getGCMPreferences(context);
			int appVersion = getAppVersion(context);
			Log.i(TAG, "Saving regId on app version " + appVersion);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(PROPERTY_REG_ID, regId);
			editor.putInt(PROPERTY_APP_VERSION, appVersion);
			editor.commit();
		}
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}

		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {

		return getSharedPreferences(SplashActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle paramBundle) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light_NoActionBar);
		} else {
			setTheme(android.R.style.Theme_Holo_Light);
			getSupportActionBar().hide();
		}
		super.onCreate(paramBundle);
		this.context = getApplicationContext();
		setContentView(R.layout.splash_activity);
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
			Log.i(TAG, regid);
			if (regid.isEmpty()) {
				new RegisterBackground().execute();
			}
			launchLauncherActivity();
		}
	}

	private void launchLauncherActivity() {
		// TODO Auto-generated method stub
		launch = new Thread() {
			public void run() {
				try {
					sleep(2000);
					Intent localIntent = new Intent(context,
							LauncherActivity.class);
					startActivity(localIntent);
					return;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		launch.start();
	}

	public void onBackPressed() {
		Process.killProcess(Process.myPid());
		Runtime.getRuntime().gc();
	}

	protected void onPause() {
		super.onPause();
		finish();
	}
}