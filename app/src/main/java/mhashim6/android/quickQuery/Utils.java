package mhashim6.android.quickQuery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by mhashim6 on 29/10/2017.
 */

public class Utils {
	public static final String QUICK_QUERY_ACTION = "QUICK_QUERY_ACTION";
	public static final String ADMOB_APP_ID = "ca-app-pub-1801049179059842~3245591274";
	public static final String FLAVOR_FULL = "full";

	public static final String GOOGLE_PLAY_LINK = "market://details?id=mhashim6.android.quickQuery";
	public static final String GOOGLE_PLAY_LINK_PRO = "market://details?id=mhashim6.android.quickQuery.full";


	static void openWebPage(Context context, String url) {
		Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(webIntent);
	}
}
