package mhashim6.android.quickQuery;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static mhashim6.android.quickQuery.Utils.QUICK_QUERY_ACTION;


/**
 * Created by mhashim6 on 25/10/2017.
 */

public class ClipboardMonitor extends JobIntentService {

	private static final int WORK_ID = 77;

	private ClipboardManager clipboardManager;

	private ImageView bubble;
	private static final WindowManager.LayoutParams LAYOUT_PARAMS = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.TYPE_PHONE,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
			PixelFormat.TRANSLUCENT);

	static {
		LAYOUT_PARAMS.gravity = Gravity.TOP | Gravity.START;
		LAYOUT_PARAMS.x = 3;
		LAYOUT_PARAMS.y = 180;
	}

	private Runnable autoHideRunnable;
//===================================================

	@Override
	public void onCreate() {
		super.onCreate();
		clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		initBubble();

		/*
		if (BuildConfig.FLAVOR.equals(FLAVOR_FULL))
			MobileAds.initialize(this, ADMOB_APP_ID);
		*/
	}

	static void enqueueWork(Context context, Intent i) {
		enqueueWork(context, ClipboardMonitor.class, WORK_ID, i);
	}

	@Override
	protected void onHandleWork(@NonNull Intent intent) {
		if (clipboardManager != null)
			clipboardManager.addPrimaryClipChangedListener(() -> {
				if (clipboardManager.hasPrimaryClip()) {
					if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("copy_key", true))
						handleNewQuery(clipboardManager.getPrimaryClip().getItemAt(0).getText());
				}
			});
	}
//===================================================

	private void initBubble() {
		bubble = new ImageView(this);
		bubble.setImageResource(R.drawable.ic_bubble);
	}
//===================================================

	private void handleNewQuery(CharSequence query) {
		try {
			URL url = new URL(query.toString()); //ignore if url.
		} catch (MalformedURLException e) {
			showBubble(query);
		}
	}

	public void showBubble(CharSequence query) {
		bubble.removeCallbacks(autoHideRunnable);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		long duration = Long.parseLong(preferences.getString("duration", "6")) * 1000;

		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		try {
			windowManager.removeView(bubble);
		} catch (Exception e) {//nah.
			Log.e("hmmmmmmmm", e.fillInStackTrace().toString());
		} finally {
			windowManager.addView(bubble, LAYOUT_PARAMS);
		}

		bubble.setOnClickListener(view -> {
			windowManager.removeView(bubble);
			startQQActivity(query);
		});
		bubble.setOnLongClickListener(view -> {//hide bubble
			windowManager.removeView(bubble);
			return true;
		});

		removeBubbleDelayed(windowManager, duration);
	}

	private void removeBubbleDelayed(WindowManager windowManager, long duration) {
		autoHideRunnable = () -> { //auto-hide bubble
			try {
				windowManager.removeView(bubble);
			} catch (Exception e) {
				Log.e("hmmmmmmmm", e.fillInStackTrace().toString());
			}
		};
		if (duration != 0)
			bubble.postDelayed(autoHideRunnable, duration);
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

}
