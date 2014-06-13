package com.rahul7teen.vit;

import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class Credits extends SherlockActivity {
	protected void onCreate(Bundle paramBundle) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light);
		} else {
			setTheme(android.R.style.Theme_Holo_Light);
		}
		super.onCreate(paramBundle);
		setContentView(R.layout.credits);
	}
}
