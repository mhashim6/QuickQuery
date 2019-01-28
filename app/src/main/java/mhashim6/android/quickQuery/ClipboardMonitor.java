package mhashim6.android.quickQuery;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.Nullable;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static mhashim6.android.quickQuery.NotificationsManagerKt.buildForegroundNotification;
import static mhashim6.android.quickQuery.Utils.IS_OREO;
import static mhashim6.android.quickQuery.Utils.QUICK_QUERY_ACTION;


/**
 * Created by mhashim6 on 25/10/2017.
 */

public class ClipboardMonitor extends Service {

	private static final int FOREGROUND_ID = 77;

	private ImageView bubble;
	private static final WindowManager.LayoutParams LAYOUT_PARAMS = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.WRAP_CONTENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
			IS_OREO ?
					WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
					: WindowManager.LayoutParams.TYPE_PHONE,
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
					| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
			PixelFormat.TRANSLUCENT);

	static {
		LAYOUT_PARAMS.gravity = Gravity.TOP | Gravity.START;
		LAYOUT_PARAMS.x = 3;
		LAYOUT_PARAMS.y = 180;
	}

	private CharSequence query;
//===================================================

	@Override
	public void onCreate() {

		super.onCreate();

		if (IS_OREO)
			startForeground(FOREGROUND_ID, buildForegroundNotification(this));
		else
			Log.i("QuickQuery", "not oreo, skipping notification.");

		initBubble();

		ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipboardManager.addPrimaryClipChangedListener(() -> handleNewQuery(clipboardManager.getPrimaryClip().getItemAt(0).getText()));

		/*
		if (BuildConfig.FLAVOR.equals(FLAVOR_FULL))
			MobileAds.initialize(this, ADMOB_APP_ID);
		*/
	}
//===================================================

	private void initBubble() {
		this.bubble = new ImageView(this);
		bubble.setImageResource(R.drawable.ic_bubble);
	}

	private void initAndAddNewBubbleToWindow() {
		initBubble();

		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowManager.addView(bubble, LAYOUT_PARAMS);
	}

	private void handleNewQuery(CharSequence query) {
		try {
			new URL(query.toString()); //ignore if url.
		} catch (MalformedURLException e) {
			this.query = query;
			showNewBubble();
		}
	}

	private void showNewBubble() {
		bubble.setVisibility(View.GONE);

		initAndAddNewBubbleToWindow();
		View newBubble = this.bubble;

		newBubble.setOnClickListener(view -> {
			newBubble.setVisibility(View.GONE);
			startQQActivity(query);
		});
		newBubble.setOnLongClickListener(view -> {//hide bubble
			newBubble.setVisibility(View.GONE);
			return true;
		});

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		long duration = Long.parseLong(preferences.getString("duration", "6")) * 1000;
		newBubble.postDelayed(() -> removeBubbleFromWindow(newBubble), duration);
	}

	private void removeBubbleFromWindow(View bubble) {
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowManager.removeView(bubble);
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
