package mhashim6.android.quickQuery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mhashim6 on 17/02/2018.
 */

public class UpdateBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent clipboardMonitorStarter = new Intent(context, ClipboardMonitor.class);
		ClipboardMonitor.enqueueWork(context, clipboardMonitorStarter);
	}
}
