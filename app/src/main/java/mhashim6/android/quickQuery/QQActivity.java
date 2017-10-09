package mhashim6.android.quickQuery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class QQActivity extends AppCompatActivity {

	private static final String GOOGLE = "https://www.google.com/search?q=";
	private static final String DUCK_DUCK_GO = "https://duckduckgo.com/?q=";
	private static final String YOUTUBE = "https://www.youtube.com/results?search_query=";

	private static final String MULTIPLE_SPACES = " +";
	private static final String PLUS_SIGN = "+";

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String query = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString();
		showDialog(query);
	}

	private void showDialog(final String query) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.app_name)
				.setIcon(R.mipmap.ic_launcher_round)
				.setItems(R.array.engines, (dialog, which) -> {
					switch (which) {
						case 0:
							google(query);
							break;

						case 1:
							duck(query);
							break;

						case 2:
							youtube(query);
							break;
					}
				})
				.setNegativeButton(R.string.cancel, (dialogInterface, i) -> finish())
				.setOnCancelListener(dialogInterface -> finish())
				.show();
	}

	private void google(String query) {
		launchWebSearch(GOOGLE, query);
	}

	private void duck(String query) {
		launchWebSearch(DUCK_DUCK_GO, query);
	}

	private void youtube(String query) {

		try {
			Intent intent = new Intent(Intent.ACTION_SEARCH);
			intent.setPackage("com.google.android.youtube");
			intent.putExtra("query", query);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);

		} catch (Exception e) { //youtube is not installed.
			launchWebSearch(YOUTUBE, query);
		}
	}

	private void launchWebSearch(String engine, String query) {
		Uri uri = Uri.parse(engine + query.trim().replaceAll(MULTIPLE_SPACES, PLUS_SIGN));
		Intent i = new Intent(Intent.ACTION_VIEW, uri);

		startActivity(i);
	}
}