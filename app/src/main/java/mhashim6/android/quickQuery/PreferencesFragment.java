package mhashim6.android.quickQuery;


import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import static mhashim6.android.quickQuery.Utils.FLAVOR_FULL;
import static mhashim6.android.quickQuery.Utils.GOOGLE_PLAY_LINK_PRO;


/**
 * Created by mhashim6 on 25/10/2017.
 */
public class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String AUTOHIDE_KEY = "duration";
	public static final String COPY_KEY = "copy_key";
	public static final String PRO_VERSION = "pro";

	public final static int PERMISSIONS_REQUEST_CODE = 65235;

	private Preference tapToTest;
	private Preference autohide;

	public PreferencesFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		preferences.registerOnSharedPreferenceChangeListener(this);

		tapToTest = findPreference("test");
		tapToTest.setEnabled(preferences.getBoolean(COPY_KEY, false));
		tapToTest.setOnPreferenceClickListener(preference -> {
			ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("QQ_LABEL", "Test text!");
			clipboard.setPrimaryClip(clip);
			return true;
		});

		autohide = findPreference(AUTOHIDE_KEY);
		if (BuildConfig.FLAVOR.equals(FLAVOR_FULL))
			autohide.setEnabled(preferences.getBoolean(COPY_KEY, false));
		else {
			Preference proVersion = findPreference(PRO_VERSION);
			proVersion.setOnPreferenceClickListener(preference -> {
				Utils.openWebPage(getActivity(), GOOGLE_PLAY_LINK_PRO);
				return true;
			});
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(COPY_KEY)) {
			boolean value = sharedPreferences.getBoolean(key, false);

			tapToTest.setEnabled(value);
			if (BuildConfig.FLAVOR.equals(FLAVOR_FULL))
				autohide.setEnabled(value);

			if (value) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
					checkDrawOverlayPermission();
			}
		}
	}
//===================================================

	@RequiresApi(api = Build.VERSION_CODES.M)
	void checkDrawOverlayPermission() {
		if (!Settings.canDrawOverlays(getContext())) {
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
					Uri.parse("package:" + getContext().getApplicationContext().getPackageName()));
			startActivityForResult(intent, PERMISSIONS_REQUEST_CODE);
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PERMISSIONS_REQUEST_CODE) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (!Settings.canDrawOverlays(getContext())) {
					Toast.makeText(getContext(), R.string.permissions_fail, Toast.LENGTH_LONG).show();
					PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(COPY_KEY, false).apply();
				}
			}
		}
	}
	//===================================================

}
