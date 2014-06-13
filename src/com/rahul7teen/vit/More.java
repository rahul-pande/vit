package com.rahul7teen.vit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Window;

@SuppressLint("InlinedApi")
@SuppressWarnings("deprecation")
public class More extends SherlockListActivity {
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT < 11) {
			setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light);
			getWindow().addFlags((int) Window.FEATURE_NO_TITLE);
		} else {
			setTheme(android.R.style.Theme_Holo_Light);
		}
		super.onCreate(savedInstanceState);

		context = this.getApplicationContext();

		String[] items = { "Credits", "Feedback", "About Us" };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);

				TextView textView = (TextView) view
						.findViewById(android.R.id.text1);
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

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
		setListAdapter(adapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		switch (position) {
		case 0:
			Intent credits = new Intent(context, Credits.class);
			startActivity(credits);
			break;
		case 1:
			String[] address = { "rahulpande7teen@gmail.com" };
			Intent feedback = new Intent("android.intent.action.SEND");
			feedback.putExtra("android.intent.extra.EMAIL", address);
			feedback.putExtra("android.intent.extra.SUBJECT",
					"VIT application Feedback");
			feedback.setType("plain/text");
			try {
				startActivity(feedback);
			} catch (Exception e) {
				Toast.makeText(context, "Please set up an email account",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 2:
			Intent about = new Intent(context, About.class);
			startActivity(about);
			break;
		}

	}

}
