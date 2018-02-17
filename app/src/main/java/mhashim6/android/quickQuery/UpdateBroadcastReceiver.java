package mhashim6.android.quickQuery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;

import static mhashim6.android.quickQuery.Utils.COPY_KEY;

/**
 * Created by mhashim6 on 17/02/2018.
 */

public class UpdateBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(COPY_KEY, false)) {
			Intent clipboardMonitorStarter = new Intent(context, ClipboardMonitor.class);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
				context.startForegroundService(clipboardMonitorStarter);
			else
				context.startService(clipboardMonitorStarter);
		}
	}
}
