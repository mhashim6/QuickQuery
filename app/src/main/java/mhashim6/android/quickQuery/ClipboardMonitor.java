package mhashim6.android.quickQuery;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.ads.MobileAds;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by mhashim6 on 25/10/2017.
 */

public class ClipboardMonitor extends Service {

	private static final String QUICK_QUERY_ACTION = "QUICK_QUERY_ACTION";
	private final static String AD_MPB_APP_ID = "ca-app-pub-1801049179059842~3245591274";

	private boolean showing;

	@Override
	public void onCreate() {
		super.onCreate();

		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		if (clipboardManager != null)
			clipboardManager
					.addPrimaryClipChangedListener(() -> {
						if (clipboardManager.hasPrimaryClip()) {
							showBubble(clipboardManager.getPrimaryClip().getItemAt(0).getText());
						}
					});
		MobileAds.initialize(this, AD_MPB_APP_ID);
	}
//===================================================

	public void showBubble(CharSequence query) {

		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		ImageView bubble = new ImageView(this);
		bubble.setImageResource(R.drawable.ic_bubble);
		bubble.setOnClickListener(view -> {
			showing = false;
			windowManager.removeView(bubble);
			startQQActivity(query);
		});
		bubble.setOnLongClickListener(view -> {//hide bubble
			showing = false;
			windowManager.removeView(bubble);
			return true;
		});

		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
		PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.START;
		params.x = 3;
		params.y = 180;

		showing = true; //show bubble
		windowManager.addView(bubble, params);

		bubble.postDelayed(() -> { //auto-hide bubble
			if (showing) {
				showing = false;
				windowManager.removeView(bubble);
			}
		}, 6000);
	}
//===================================================

	private void startQQActivity(CharSequence query) {
		Intent qqStarter = new Intent(this, QQActivity.class);
		qqStarter.setAction(QUICK_QUERY_ACTION);
		qqStarter.putExtra(Intent.EXTRA_TEXT, query);
		qqStarter.setFlags(FLAG_ACTIVITY_NEW_TASK);
		startActivity(qqStarter);
	}
//===================================================

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
