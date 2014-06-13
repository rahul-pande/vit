package com.rahul7teen.vit;

import java.io.File;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

@SuppressLint({ "ValidFragment" })
public class ContentFragment extends SherlockListFragment

{
	long reference;
	Context context;
	String[] links;
	String[] texts;

	public ContentFragment() {
	}

	public ContentFragment(Context paramContext) {
		this.context = paramContext;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void downloadFile(String paramString) {
		Toast.makeText(this.context, "Downloading...", Toast.LENGTH_SHORT)
				.show();
		DownloadManager localDownloadManager = (DownloadManager) this.context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request localRequest = new DownloadManager.Request(
				Uri.parse(paramString));
		final String fileName = paramString.substring(
				1 + paramString.lastIndexOf("/")).replace("%20", " ");
		File localFile = new File(Environment
				.getExternalStoragePublicDirectory(
						Environment.DIRECTORY_DOWNLOADS).toString()
				+ "/" + fileName);
		if (localFile.exists()) {
			Toast.makeText(this.context, "File Exists.Overwriting..",
					Toast.LENGTH_SHORT).show();
			localFile.delete();
		}
		localRequest.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, fileName);
		if (!(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB))
			localRequest
					.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

		reference = localDownloadManager.enqueue(localRequest);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			IntentFilter localIntentFilter = new IntentFilter(
					"android.intent.action.DOWNLOAD_COMPLETE");
			BroadcastReceiver receiver = new BroadcastReceiver() {
				public void onReceive(Context paramContext, Intent paramIntent) {
					long downloadReference = paramIntent.getLongExtra(
							"extra_download_id", -1);
					if (reference == downloadReference) {
						Intent localIntent = new Intent();
						localIntent
								.setAction("android.intent.action.VIEW_DOWNLOADS");
						PendingIntent localPendingIntent = PendingIntent
								.getActivity(paramContext, 0, localIntent, 0);
						NotificationManager localNotificationManager = (NotificationManager) paramContext
								.getSystemService(Context.NOTIFICATION_SERVICE);
						NotificationCompat.Builder build = new NotificationCompat.Builder(
								context).setContentTitle(fileName)
								.setContentText("Download complete")
								.setContentIntent(localPendingIntent)
								.setAutoCancel(true)
								.setSmallIcon(R.drawable.download);
						Notification noti = build.build();
						noti.defaults = Notification.DEFAULT_ALL;
						localNotificationManager.notify(0, noti);
					}
				}
			};
			this.context.registerReceiver(receiver, localIntentFilter);
		}
	}

	private boolean isFile(String paramString) {
		return paramString.substring(paramString.lastIndexOf("/"))
				.contains(".");
	}

	public void onActivityCreated(Bundle paramBundle) {
		super.onActivityCreated(paramBundle);
		this.texts = getArguments().getStringArray("texts");
		this.links = getArguments().getStringArray("links");
		if (this.texts != null)
			setListAdapter(new ArrayAdapter<String>(getSherlockActivity(),
					android.R.layout.simple_list_item_1, this.texts) {

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					View view = super.getView(position, convertView, parent);

					TextView textView = (TextView) view
							.findViewById(android.R.id.text1);
					textView.setTextColor(Color.BLACK);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
					/*
					 * int paddingPixel = 45; float density =
					 * context.getResources() .getDisplayMetrics().density; int
					 * paddingDp = (int) (paddingPixel * density); LayoutParams
					 * params = new LayoutParams(
					 * AbsListView.LayoutParams.MATCH_PARENT, paddingDp);
					 * view.setLayoutParams(params);
					 */

					return view;
				}
			});
	}

	@Override
	public void onListItemClick(ListView paramListView, View paramView,
			int paramInt, long paramLong) {
		super.onListItemClick(paramListView, paramView, paramInt, paramLong);
		String link = links[paramInt];
		if (isFile(link)) {
			if ((!link.contains(".com")) && (!link.contains(".htm"))
					&& (!link.contains(".html")))
				downloadFile(link);
			else
				startActivity(new Intent("android.intent.action.VIEW",
						Uri.parse(link)));
		} else {
			Bundle localBundle = new Bundle();
			localBundle.putString("link", link);
			Intent localIntent = new Intent(context, GenericListActivity.class);
			localIntent.putExtra("packet", localBundle);
			startActivity(localIntent);
		}
	}
}
