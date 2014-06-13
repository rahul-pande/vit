package com.rahul7teen.vit;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class ShowcaseFragment extends Fragment {
	Bitmap bmp = null;
	ImageView caseImageView;
	TextView caseTextView;
	ImageLoader task;
	ViewGroup rootView;
	String url;
	String title;
	int width;

	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		Bundle localBundle = getArguments();
		url = localBundle.getString("url");
		title = localBundle.getString("title");
		task = new ImageLoader();
		task.execute(url);
	}

	public View onCreateView(LayoutInflater paramLayoutInflater,
			ViewGroup paramViewGroup, Bundle paramBundle) {
		if (this.rootView == null) {
			rootView = ((ViewGroup) paramLayoutInflater.inflate(
					R.layout.showcase_fragment, paramViewGroup, false));
			caseImageView = ((ImageView) this.rootView
					.findViewById(R.id.caseImg));
			caseTextView = ((TextView) this.rootView
					.findViewById(R.id.caseText));
			caseImageView.setImageResource(R.drawable.loading);
			caseTextView.setText(title);
			caseTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
			caseTextView.setIncludeFontPadding(false);
		}
		return this.rootView;
	}

	public void onPause() {
		super.onPause();
		task.cancel(true);
	}

	public void onResume() {
		super.onResume();
		if (bmp == null) {
			task = new ImageLoader();
			task.execute(url);
		}
	}

	class ImageLoader extends AsyncTask<String, Integer, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... params) {
			URL url;
			Bitmap bmp = null;
			InputStream is = null;
			try {
				url = new URL(params[0]);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				/*
				 * connection.setReadTimeout(1000);
				 * connection.setConnectTimeout(1000);
				 * connection.setRequestMethod("GET");
				 */
				if (isCancelled()) {
					return null;
				}
				connection.connect();
				is = connection.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				bmp = BitmapFactory.decodeStream(bis);
				is.close();
				bis.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bmp;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				bmp = bitmap;
				caseImageView.setScaleType(ScaleType.FIT_XY);
				caseImageView.setImageBitmap(bitmap);
			} else {
				task = new ImageLoader();
				task.execute(url);
			}
		}
	}
}