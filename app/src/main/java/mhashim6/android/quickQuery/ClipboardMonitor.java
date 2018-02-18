package mhashim6.android.quickQuery;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static mhashim6.android.quickQuery.Utils.IS_OREO;
import static mhashim6.android.quickQuery.Utils.QUICK_QUERY_ACTION;


/**
 * Created by mhashim6 on 25/10/2017.
 */

public class ClipboardMonitor extends Service {

	private static final int FOREGROUND_ID = 77;
	private static final String CHANNEL_ID = "QUICK_QUERY_CHANNEL";

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
			startForeground(FOREGROUND_ID, buildForegroundNotification());
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

	@TargetApi(26)
	private Notification buildForegroundNotification() {
		createChannelForOreo();

		Intent MainActivityStarter = new Intent(this, MainActivity.class);
		PendingIntent notificationAction = PendingIntent.getActivity(this, 0, MainActivityStarter, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
		builder.setOngoing(true)
				.setContentTitle(getString(R.string.app_name))
				.setContentIntent(notificationAction)
				.setSmallIcon(R.drawable.notification_search)
				.setPriority(Notification.PRIORITY_MIN)
				.setContentText(getString(R.string.running));
		return (builder.build());
	}

	@TargetApi(26)
	private void createChannelForOreo() {
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		String title = getString(R.string.app_name);
		String description = getString(R.string.running);

		NotificationChannel channel = new NotificationChannel(CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT);
		channel.setDescription(description);
		notificationManager.createNotificationChannel(channel);
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
