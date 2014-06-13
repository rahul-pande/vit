package com.rahul7teen.vit;

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class LauncherActivity extends SherlockFragmentActivity {
	public static int[] size;
	ViewPager casePager;
	PagerAdapter casePagerAdapter;
	ViewPager contentPager;
	PagerAdapter contentPagerAdapter;
	Context context;
	ProgressDialog dialog;
	LinearLayout emptyLayout;
	String[] latestTexts;
	final String siteURL = "http://www.vitacademics.in";
	DocLoaderTask task;
	Document vitDoc;
	int cPage = 1;
	Timer timer;
	int initTime = 0;

	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle paramBundle) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light);
		} else {
			setTheme(android.R.style.Theme_Holo_Light);
		}
		super.onCreate(paramBundle);
		this.context = getApplicationContext();
		if (!isConnected()) {
			Toast.makeText(this.context, "Check your connection and retry.",
					Toast.LENGTH_SHORT).show();
			finish();
		}
		setActionBar();

		if (vitDoc == null) {
			task = new DocLoaderTask();
			task.execute(siteURL);

			dialog = ProgressDialog.show(this, "", "Loading...", true, true,
					new DialogInterface.OnCancelListener() {
						public void onCancel(
								DialogInterface paramDialogInterface) {
							task.cancel(true);
							if (vitDoc == null) {
								finish();
							}
						}
					});
			dialog.setCanceledOnTouchOutside(false);

		}
	}

	private void setActionBar() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Vishwakarma Institute");
		actionBar.setSubtitle("of Technology");
		actionBar.hide();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.action_refresh:
			LinearLayout emptyLayout = new LinearLayout(this);
			this.setContentView(emptyLayout);

			task = new DocLoaderTask();
			task.execute(siteURL);
			dialog.setMessage("Refreshing...");
			dialog.show();
			break;

		case R.id.action_share:
			confirmShare();
			break;
		case R.id.action_more:
			Intent more = new Intent(context, More.class);
			startActivity(more);
			break;
		case R.id.action_settings:
			Intent settings = new Intent(context, GeneralPreferences.class);
			startActivity(settings);
			break;
		}

		return super.onOptionsItemSelected(menuItem);
	}

	private void confirmShare() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				LauncherActivity.this);
		alertDialog.setTitle("Confirm sharing");
		alertDialog.setMessage("Share this application?");
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
				"Check out VIT Pune android app at http://goo.gl/onYmzu");
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	private void initializeFragments() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean showCase = pref.getBoolean("showcase_pref", true);
		if (showCase) {
			setContentView(R.layout.main_layout);
			setShowcasePager();
		} else {
			setContentView(R.layout.alt_main_layout);
		}
		setContentPager();
	}

	private void setShowcasePager() {
		// TODO Auto-generated method stub
		casePager = ((ViewPager) findViewById(R.id.case_pager));
		casePagerAdapter = new ShowcasePagerAdapter(
				getSupportFragmentManager(), vitDoc);
		casePager.setAdapter(casePagerAdapter);
		casePager.setOffscreenPageLimit(10);
		casePager.setPageTransformer(true, new ZoomOutPageTransformer());
		casePager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageSelected(int arg0) {
				cPage = arg0;
			}

		});
		switchPager(2500);

	}

	private void setContentPager() {
		// TODO Auto-generated method stub
		contentPager = ((ViewPager) findViewById(R.id.content_pager));
		contentPagerAdapter = new ContentPagerAdapter(
				getSupportFragmentManager(), vitDoc, context);
		contentPager.setAdapter(this.contentPagerAdapter);
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

	private void switchPager(int time) {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						if (casePagerAdapter.getCount() != 0) {
							casePager.setCurrentItem(cPage++
									% casePagerAdapter.getCount());
						}
					}
				});
			}
		}, 5000, time);
	}

	public class DocLoaderTask extends AsyncTask<String, Void, Document> {

		protected Document doInBackground(String... params) {
			Document doc = null;
			try {
				doc = Jsoup.parse(new URL(params[0]), 10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return doc;
		}

		protected void onPostExecute(Document doc) {
			vitDoc = doc;
			if (vitDoc != null) {
				getSupportActionBar().show();
				dialog.dismiss();
				initializeFragments();
			} else {
				if (!isConnected()) {
					Toast.makeText(context, "Check your connection and retry.",
							Toast.LENGTH_SHORT).show();
					finish();
				} else {
					// dialog.setMessage("Connection error! Retrying...");
					task = new DocLoaderTask();
					task.execute(siteURL);
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (dialog.isShowing()) {
			task.cancel(true);
			dialog.dismiss();
		} else {
			confirmExit();
			Runtime.getRuntime().gc();

		}
	}

	private void confirmExit() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				LauncherActivity.this);
		alertDialog.setMessage("Exit the application?");
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();

						new Thread() {
							public void run() {
								try {
									sleep(2000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						}.start();
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
}