package mhashim6.android.quickQuery;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static mhashim6.android.quickQuery.UtilsKt.COPY_KEY;
import static mhashim6.android.quickQuery.UtilsKt.DONATE_LINK;
import static mhashim6.android.quickQuery.UtilsKt.GOOGLE_PLAY_LINK_PRO;
import static mhashim6.android.quickQuery.UtilsKt.openWebPage;

public class MainActivity extends AppCompatActivity {

	public final static int PERMISSIONS_REQUEST_CODE = 65235;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getFragmentManager().beginTransaction().replace(R.id.content_frame, new PreferencesFragment()).commit();
		initToolbar();

		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(COPY_KEY, false))
			startClipboardService();

		PreferenceManager.getDefaultSharedPreferences(this).
				registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
					if (key.equals(COPY_KEY))
						if (sharedPreferences.getBoolean(COPY_KEY, true)) {
							startCopyToLaunch();
						} else
							stopClipboardService();
				});
	}

	private void initToolbar() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
		setSupportActionBar(toolbar);
	}
//===================================================

	private void startCopyToLaunch() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

			if (Settings.canDrawOverlays(this))
				startClipboardService();

			else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle(R.string.request_permissions);
				dialog.setPositiveButton(R.string.grant, (dialogInterface, i) -> requestDrawOverlayPermission());
				dialog.setNegativeButton(R.string.deny, (dialogInterface, i) -> disableCopyToLaunch());
				dialog.setOnCancelListener(dialogInterface -> disableCopyToLaunch());
				dialog.show();
			}
	}

	private void disableCopyToLaunch() {
		PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(COPY_KEY, false).apply();
	}
//===================================================

	@RequiresApi(api = Build.VERSION_CODES.M)
	private void requestDrawOverlayPermission() {
		Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getApplicationContext().getPackageName()));
		startActivityForResult(intent, PERMISSIONS_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PERMISSIONS_REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			if (Settings.canDrawOverlays(this))
				startClipboardService();
			else
				disableCopyToLaunch();
	}
//===================================================

	private void startClipboardService() {
		Intent clipboardMonitorStarter = new Intent(this, ClipboardMonitor.class);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			startForegroundService(clipboardMonitorStarter);
		else
			startService(clipboardMonitorStarter);
	}

	private void stopClipboardService() {
		Intent clipboardMonitorStarter = new Intent(this, ClipboardMonitor.class);
		stopService(clipboardMonitorStarter);
	}
//===================================================

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		MenuItem rate = menu.findItem(R.id.rate_item);
		rate.setOnMenuItemClickListener(item -> {
			openWebPage(this, GOOGLE_PLAY_LINK_PRO);
			return true;
		});

		MenuItem donate = menu.findItem(R.id.donate_item);
		donate.setOnMenuItemClickListener(item -> {
			openWebPage(this, DONATE_LINK);
			return true;
		});
		return super.onCreateOptionsMenu(menu);
	}
//===================================================

}
