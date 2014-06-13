package com.rahul7teen.vit;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class GeneralPreferences extends SherlockPreferenceActivity {

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light);
		} else {
			setTheme(android.R.style.Theme_Holo_Light);
		}
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		final Preference pref = (Preference) findPreference("showcase_pref");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Toast.makeText(getBaseContext(),
						"Please restart the app for changes to take effect.",
						Toast.LENGTH_SHORT).show();
				return false;
			}
		});
	}

}
