package mhashim6.android.quickQuery;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class QQActivity extends AppCompatActivity {

	private static final String GOOGLE = "https://www.google.com/search?q=";
	private static final String DUCK_DUCK_GO = "https://duckduckgo.com/?q=";
	private static final String YOUTUBE = "https://www.youtube.com/results?search_query=";
	private static final String GOOGLE_PLAY = "market://search?q=";

	private static final String MULTIPLE_SPACES = " +";
	private static final String PLUS_SIGN = "+";
	private static final String YOUTUBE_PACKAGE = "com.google.android.youtube";

	private static AdRequest adRequest;
	private AdView adView;

	private String query;
	private String dialogTitle;
//===================================================

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent starter = getIntent();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Intent.ACTION_PROCESS_TEXT.equals(starter.getAction()))
			query = starter.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString();
		else
			query = starter.getCharSequenceExtra(Intent.EXTRA_TEXT).toString();
		dialogTitle = getResources().getString(R.string.app_name) + " | " + query; //TODO

		requestAds();
		showDialog();
	}
//===================================================

	private void requestAds() {
		String[] keywords = query.split(MULTIPLE_SPACES);

		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("5595A1D3B92A1BF4D4E4D1F164AD1A3F");

		for (String keyword : keywords)
			adRequestBuilder.addKeyword(keyword);

		adRequest = adRequestBuilder.build();
	}
//===================================================

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(dialogTitle)
				.setView(inflateDialogView())
				.setIcon(R.drawable.ic_bubble)
				.setNegativeButton(R.string.cancel, (dialogInterface, i) -> finish())
				.setOnCancelListener(dialogInterface -> finish())
				.show().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT); //Controlling width and height.
	}

	private View inflateDialogView() {
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.qq_dialog, null);

		/*ListView*/
		ListView enginesListView = dialogView.findViewById(R.id.engines_list_view);

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.engines));
		enginesListView.setAdapter(arrayAdapter);
		enginesListView.setOnItemClickListener((adapterView, view, pos, id) -> {
			switch (pos) {
				case 0:
					google(query);
					break;

				case 1:
					duck(query);
					break;

				case 2:
					youtube(query);
					break;

				case 3:
					googlePlay(query);
					break;
			}
		});

		/*AdView*/
		adView = dialogView.findViewById(R.id.adView);
		adView.loadAd(adRequest);

		return dialogView;
	}
//===================================================

	private void google(String query) {
	/*	Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		intent.putExtra(SearchManager.QUERY, query);

		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		} else {
			launchWebSearch(GOOGLE, query);
		}*/
		launchWebSearch(GOOGLE, query);
	}

	private void duck(String query) {
		launchWebSearch(DUCK_DUCK_GO, query);
	}

	private void youtube(String query) {

		Intent intent = new Intent(Intent.ACTION_SEARCH);
		intent.setPackage(YOUTUBE_PACKAGE);
		intent.putExtra("query", query);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		} else { //youtube is not installed.
			launchWebSearch(YOUTUBE, query);
		}
	}

	private void googlePlay(String query) {
		launchWebSearch(GOOGLE_PLAY, query);
	}

	private void launchWebSearch(String engine, String query) {
		Uri uri = Uri.parse(engine + query.trim().replaceAll(MULTIPLE_SPACES, PLUS_SIGN));
		Intent i = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(i);
	}
}