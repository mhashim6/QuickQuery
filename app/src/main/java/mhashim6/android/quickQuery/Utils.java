package mhashim6.android.quickQuery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

/**
 * Created by mhashim6 on 29/10/2017.
 */

class Utils {
	static final String QUICK_QUERY_ACTION = "QUICK_QUERY_ACTION";
	static final String GOOGLE_PLAY_LINK_PRO = "market://details?id=mhashim6.android.quickQuery.full";
	static final String DONATE_LINK = "http://paypal.com/paypalme/mhashim6";

	static final String COPY_KEY = "copy_key";

	static final boolean IS_OREO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;

	static void openWebPage(Context context, String url) {
		Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(webIntent);
	}
}
