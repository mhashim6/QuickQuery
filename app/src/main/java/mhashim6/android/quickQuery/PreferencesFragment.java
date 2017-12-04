package mhashim6.android.quickQuery;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


/**
 * Created by mhashim6 on 25/10/2017.
 */
public class PreferencesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String AUTOHIDE_KEY = "duration";
	public static final String COPY_KEY = "copy_key";
	//public static final String PRO_VERSION = "pro";


	private Preference tapToTest, autohide;

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
		//if (BuildConfig.FLAVOR.equals(FLAVOR_FULL))
		autohide.setEnabled(preferences.getBoolean(COPY_KEY, false));
		/*else {
			Preference proVersion = findPreference(PRO_VERSION);
			proVersion.setOnPreferenceClickListener(preference -> {
				Utils.openWebPage(getActivity(), GOOGLE_PLAY_LINK_PRO);
				return true;
			});
		}*/
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(COPY_KEY)) {
			boolean value = sharedPreferences.getBoolean(key, false);

			tapToTest.setEnabled(value);
			//	if (BuildConfig.FLAVOR.equals(FLAVOR_FULL))
			autohide.setEnabled(value);
		}
	}
//===================================================

}
