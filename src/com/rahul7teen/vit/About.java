package com.rahul7teen.vit;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

public class About extends Activity {
	protected void onCreate(Bundle paramBundle) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			this.setTheme(android.R.style.Theme_Holo_Light_Dialog);
		}
		super.onCreate(paramBundle);
		setContentView(R.layout.about);
	}
}