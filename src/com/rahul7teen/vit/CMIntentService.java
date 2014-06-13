package com.rahul7teen.vit;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class CMIntentService extends IntentService {
	Context context;
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	public static final String TAG = "GCM Demo";

	public CMIntentService() {
		super("CMIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Bundle extras = intent.getExtras();
		String msg = intent.getStringExtra("message");
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {

			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				sendNotification(msg);
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		CMBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		String title = "News Update";
		String url = null;
		try {
			JSONObject json = new JSONObject(msg);
			title = json.getString("msg_title");
			url = json.getString("msg_url");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_stat_notify)
				.setContentTitle(title).setAutoCancel(true);
		Intent myintent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, myintent,
				Intent.FLAG_ACTIVITY_NEW_TASK);

		if (url.startsWith("http") || url.startsWith("www")) {
			myintent = new Intent(this, GenericListActivity.class);
			myintent.putExtra("msg_url", url);
			contentIntent = PendingIntent.getActivity(this, 0, myintent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentText("Click to check it out");
		}
		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
}