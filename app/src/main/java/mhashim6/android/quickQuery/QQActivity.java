package mhashim6.android.quickQuery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.Set;

import static mhashim6.android.quickQuery.Utils.GOOGLE_PLAY_LINK_PRO;
import static mhashim6.android.quickQuery.Utils.openWebPage;


public class QQActivity extends AppCompatActivity {

	private static final String GOOGLE = "https://www.google.com/search?q=";
	private static final String DUCK_DUCK_GO = "https://duckduckgo.com/?q=";
	private static final String YOUTUBE = "https://www.youtube.com/results?search_query=";
	private static final String GOOGLE_PLAY = "market://search?q=";

	private static final String MULTIPLE_SPACES = " +";
	private static final String PLUS_SIGN = "+";
	private static final String YOUTUBE_PACKAGE = "com.google.android.youtube";
//	private static final String GOOGLE_IMAGE = "https://images.google.com/searchbyimage?image_url=";

	private String[] allEngines;

	private SharedPreferences preferences;

	//private static AdRequest adRequest;
	//private AdView adView;

	private String query;
//===================================================

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		allEngines = getResources().getStringArray(R.array.engines);

		Intent starter = getIntent();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Intent.ACTION_PROCESS_TEXT.equals(starter.getAction()))
			query = starter.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString();
		else
			query = starter.getCharSequenceExtra(Intent.EXTRA_TEXT).toString();

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		//requestAds();
		showDialog();
	}
//===================================================

/*	private void requestAds() {
		String[] keywords = query.split(MULTIPLE_SPACES);

		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
				//.addTestDevice("5595A1D3B92A1BF4D4E4D1F164AD1A3F");

		for (String keyword : keywords)
			adRequestBuilder.addKeyword(keyword);

		adRequest = adRequestBuilder.build();
	}*/
//===================================================

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(query)
				.setView(inflateDialogView())
				.setIcon(R.drawable.ic_bubble)
				//.setNegativeButton(R.string.cancel, (dialogInterface, i) -> finish())
				.setOnCancelListener(dialogInterface -> finish())
				//.setPositiveButton(R.string.settings,(dialogInterface, i) -> startActivity(new Intent(this, MainActivity.class)))
				.show().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT); //Controlling width and height.
	}

	private View inflateDialogView() {
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.qq_dialog, null);

		/*
		if (!BuildConfig.FLAVOR.equals(FLAVOR_FULL)) {
			adView = dialogView.findViewById(R.id.adView);
			adView.loadAd(adRequest);
		}
*/
		/*ListView*/
		ListView enginesListView = dialogView.findViewById(R.id.engines_list_view);
		Set<String> engines = preferences.getStringSet("engines", Collections.emptySet());
		String[] enginesArray = new String[engines.size()];

		for (int i = 0, j = 0; i < allEngines.length; i++) { //for order.
			String engine = allEngines[i];
			if (engines.contains(engine)) {
				enginesArray[j] = engine;
				j++;
			}
		}
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1,
				enginesArray);
		enginesListView.setAdapter(arrayAdapter);
		enginesListView.setOnItemClickListener((adapterView, view, pos, id) -> {
			String selected = enginesArray[pos];
			if ("Google".equals(selected))
				google(query);
			else if ("DuckDuckGo".equals(selected))
				duck(query);
			else if ("YouTube".equals(selected))
				youtube(query);
			else if ("Google Play".equals(selected))
				googlePlay(query);
			else {
				openWebPage(this, GOOGLE_PLAY_LINK_PRO);
			}
		});

		return dialogView;
	}
//===================================================

	private void google(String query) {
	/*	Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		intent.putExtra(SearchManager.QUERY, query);

		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		} else {
			webSearch(GOOGLE, query);
		}*/
		webSearch(GOOGLE, query);
	}

	private void duck(String query) {
		webSearch(DUCK_DUCK_GO, query);
	}

	private void youtube(String query) {

		Intent intent = new Intent(Intent.ACTION_SEARCH);
		intent.setPackage(YOUTUBE_PACKAGE);
		intent.putExtra("query", query);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		} else { //youtube is not installed.
			webSearch(YOUTUBE, query);
		}
	}

	private void googlePlay(String query) {
		webSearch(GOOGLE_PLAY, query);
	}

	private void webSearch(String engine, String query) {
		Uri uri = Uri.parse(engine + query.trim().replaceAll(MULTIPLE_SPACES, PLUS_SIGN));
		launchURI(uri);
	}

	private void launchURI(Uri uri) {
		Intent i = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(i);
	}
}