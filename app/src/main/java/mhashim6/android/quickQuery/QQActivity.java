package mhashim6.android.quickQuery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Collections;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

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

		showDialog();
	}
//===================================================

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setView(inflateDialogView())
				.setIcon(R.drawable.ic_bubble)
				.setTitle("Search for")
				//.setNegativeButton(R.string.cancel, (dialogInterface, i) -> finish())
				.setOnCancelListener(dialogInterface -> finish())
				//.setPositiveButton(R.string.settings,(dialogInterface, i) -> startActivity(new Intent(this, MainActivity.class)))
				.show().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT); //Controlling width and height.
	}

	private View inflateDialogView() {
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.qq_dialog, null);

		/*query*/
		AppCompatEditText queryEditText = dialogView.findViewById(R.id.query_edit_text);
		queryEditText.setText(query);
		queryEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence newQuery, int i, int i1, int i2) {
				query = newQuery.toString();
			}

			@Override
			public void afterTextChanged(Editable editable) {

			}
		});

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
		Utils.openWebPage(this, engine + query.trim().replaceAll(MULTIPLE_SPACES, PLUS_SIGN));
	}

}