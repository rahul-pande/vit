package com.rahul7teen.vit;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

@SuppressWarnings("deprecation")
public class GenericListActivity extends SherlockListActivity {
	Context context;
	ProgressDialog dialog;
	long enqueue;
	Document genericDoc;
	int i, j;
	GenericItem item;
	int response = -1;
	GenericDocLoaderTask task;
	String url;
	long reference;
	JSONObject json;

	class GenericItem {
		String[] itemLinks;
		String[] itemTexts;
	}

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle paramBundle) {
		if (Build.VERSION.SDK_INT < 11) {
			setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light);
			getWindow().addFlags((int) Window.FEATURE_NO_TITLE);
		} else {
			setTheme(android.R.style.Theme_Holo_Light);
		}
		super.onCreate(paramBundle);
		context = getApplicationContext();
		if (!isConnected()) {
			Toast.makeText(context, "Check your connection and retry.",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.hide();
		actionBar.setHomeButtonEnabled(true);
		try {
			url = getIntent().getBundleExtra("packet").getString("link");
		} catch (NullPointerException e) {
			Log.d("GCMDemo", "Intent url null");
		}

		if (url == null) {
			url = getIntent().getExtras().getString("msg_url");
			if (!(url.startsWith("http"))) {
				url = "http://" + url;
			}
		}
		dialog = ProgressDialog.show(this, "", "Loading...", true, true,
				new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface paramDialogInterface) {
						Toast.makeText(context, "Cancelled!",
								Toast.LENGTH_SHORT).show();
						finish();
					}
				});

		dialog.setCanceledOnTouchOutside(false);
		task = new GenericDocLoaderTask();
		task.execute(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		String link = item.itemLinks[position];
		if (isFile(link)) {
			if (!((link.contains(".com")) || (link.contains("htm")) || (link
					.contains("html")))) {
				downloadFile(link);
			} else {
				startActivity(new Intent("android.intent.action.VIEW",
						Uri.parse(link)));
			}
		} else {
			Bundle localBundle = new Bundle();
			localBundle.putString("link", link);
			Intent localIntent = new Intent(context, GenericListActivity.class);
			localIntent.putExtra("packet", localBundle);
			startActivity(localIntent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
		switch (paramMenuItem.getItemId()) {
		case R.id.action_refresh:
			task = new GenericDocLoaderTask();
			task.execute(url);
			dialog.setMessage("Refreshing...");
			dialog.show();
			break;
		case R.id.action_more:
			Intent more = new Intent(context, More.class);
			startActivity(more);
			break;
		case android.R.id.home:
			finish();
			break;
		case R.id.action_share:
			confirmShare();
			break;
		case R.id.action_settings:
			Intent settings = new Intent(context, GeneralPreferences.class);
			startActivity(settings);
			break;
		}

		return super.onOptionsItemSelected(paramMenuItem);

	}

	private void confirmShare() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				GenericListActivity.this);
		alertDialog.setTitle("Confirm sharing");
		alertDialog.setMessage("Share " + genericDoc.title() + " with others?");
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						shareContent();
					}
				});
		alertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
	}

	private void shareContent() {
		// TODO Auto-generated method stub
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT,
				"Check out: " + genericDoc.title() + " " + url);
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!task.isCancelled()) {
			task.cancel(true);
		}

	}

	public class GenericDocLoaderTask extends AsyncTask<String, Void, Document> {

		@Override
		protected Document doInBackground(String... params) {
			Document doc = null;
			try {
				URL url = new URL(params[0]);
				doc = Jsoup.parse(url, 10000);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setReadTimeout(5000);
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				connection.connect();
				response = connection.getResponseCode();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return doc;
		}

		@Override
		protected void onPostExecute(Document paramDocument) {
			genericDoc = paramDocument;
			if (genericDoc != null) {
				dialog.dismiss();
				item = getGenericItem(genericDoc);
				if (item.itemTexts.length == 0) {
					Toast.makeText(context, "No downloadable content!",
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					getSupportActionBar().show();
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							context, android.R.layout.simple_list_item_1,
							item.itemTexts) {

						@Override
						public View getView(int position, View convertView,
								ViewGroup parent) {
							View view = super.getView(position, convertView,
									parent);

							TextView textView = (TextView) view
									.findViewById(android.R.id.text1);
							textView.setTextColor(Color.BLACK);
							textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
							// textView.setMaxLines(5);

							android.view.ViewGroup.LayoutParams params = textView
									.getLayoutParams();
							if (params.height > 0) {
								int height = params.height;
								params.height = LayoutParams.WRAP_CONTENT;
								textView.setLayoutParams(params);
								textView.setMinHeight(height);
							}
							return view;
						}
					};
					getSupportActionBar().setTitle(genericDoc.title());
					setListAdapter(adapter);
				}
			} else {
				if (response != 200) {
					Toast.makeText(
							context,
							response == -1 ? "Not available"
									: (response + " error"), Toast.LENGTH_SHORT)
							.show();
					finish();
				} else {
					if (!isConnected()) {
						Toast.makeText(context,
								"Check your connection and retry.",
								Toast.LENGTH_SHORT).show();
						finish();
					} else {
						// dialog.setMessage("Connection error! Retrying...");
						task = new GenericDocLoaderTask();
						task.execute(url);
					}
				}
			}
		}
	}

	private GenericItem getGenericItem(Document paramDocument) {
		GenericItem genericItem = new GenericItem();

		Elements texts = paramDocument
				.select("div#content_effect div#middle-column a[href]");
		Elements links = paramDocument
				.select("div#content_effect div#middle-column a[href]");

		/*
		 * Elements texts = paramDocument
		 * .select("div.item-page.item-page a[href]"); Elements links =
		 * paramDocument .select("div.item-page.item-page a[href]");
		 */
		int size = texts.size();

		for (i = 0; i < texts.size(); i++) {
			if ((links.get(i).attr("href").contains("="))
					|| (links.get(i).attr("href").contains(";"))) {
				size--;
			}

		}
		genericItem.itemTexts = new String[size];
		genericItem.itemLinks = new String[size];

		for (i = 0, j = 0; j < size;) {
			if ((links.get(i).attr("href").contains("="))
					|| (links.get(i).attr("href").contains(";"))) {
				i++;
			} else {

				genericItem.itemTexts[j] = texts.get(i).text();
				String str = links.get(i).attr("href");
				if (!(str.startsWith("http")) || (str.startsWith("www"))) {
					str = "http://www.vitacademics.in" + str;
				}
				genericItem.itemLinks[j] = str.replace(" ", "%20");
				j++;
				i++;
			}
		}
		return genericItem;

	}

	@SuppressLint("NewApi")
	private void downloadFile(String paramString) {
		Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();
		DownloadManager localDownloadManager = (DownloadManager) context
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
			Toast.makeText(context, "File Exists.Overwriting..",
					Toast.LENGTH_SHORT).show();
			localFile.delete();
		}
		localRequest.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, fileName);
		if (Build.VERSION.SDK_INT >= 11)
			localRequest
					.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

		reference = localDownloadManager.enqueue(localRequest);
		if (Build.VERSION.SDK_INT < 11) {
			IntentFilter intentFilter = new IntentFilter(
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
						noti.defaults = Notification.DEFAULT_SOUND;
						localNotificationManager.notify(0, noti);
					}
				}
			};
			context.registerReceiver(receiver, intentFilter);
		}
	}

	private boolean isFile(String paramString) {
		return paramString.substring(paramString.lastIndexOf("/"))
				.contains(".");
	}

	boolean isConnected() {
		NetworkInfo localNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		boolean value = true;
		if ((localNetworkInfo == null) || (!localNetworkInfo.isConnected())) {
			value = false;
		}
		return value;
	}
}