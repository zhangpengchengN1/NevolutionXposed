package com.oasisfeng.nevo.xposed;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Map;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.N;

public class MainPreference extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager manager = getPreferenceManager();
		// if (SDK_INT >= N) manager.setStorageDeviceProtected();
		Map<String, ?> map = manager.getSharedPreferences().getAll();
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			Log.d("inspect", entry.getKey() + "=>" + entry.getValue());
		}
		addPreferencesFromResource(R.xml.main_preference);
	}
}