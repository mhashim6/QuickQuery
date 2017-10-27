package mhashim6.android.quickQuery;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	public final static int PERMISSIONS_REQUEST_CODE = 65235;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent clipboardMonitorStarter = new Intent(this, ClipboardMonitor.class);

		startService(clipboardMonitorStarter);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			checkDrawOverlayPermission();
	}
//===================================================

	@RequiresApi(api = Build.VERSION_CODES.M)
	public void checkDrawOverlayPermission() {
		if (!Settings.canDrawOverlays(this)) {
			Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
					Uri.parse("package:" + getApplicationContext().getPackageName()));
			startActivityForResult(intent, PERMISSIONS_REQUEST_CODE);
		} else {
			//TODO
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PERMISSIONS_REQUEST_CODE) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (!Settings.canDrawOverlays(this)) {
					//TODO
				}
			}
		}
	}
	//===================================================

}
