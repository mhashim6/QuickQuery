package mhashim6.android.quickQuery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by mhashim6 on 25/10/2017.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent clipboardMonitorStarter = new Intent(context, ClipboardMonitor.class);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			context.startForegroundService(clipboardMonitorStarter);
		else
			context.startService(clipboardMonitorStarter);
	}
}
