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
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
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

public class ClipboardMonitor extends Service {

	private static final int FOREGROUND_ID = 77;
	private static final String CHANNEL_ID = "QUICK_QUERY_CHANNEL";

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
		startForeground(FOREGROUND_ID, buildForegroundNotification());

		clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		initBubble();

		if (clipboardManager != null)
			clipboardManager.addPrimaryClipChangedListener(() -> {
				if (clipboardManager.hasPrimaryClip())
					if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("copy_key", true))
						handleNewQuery(clipboardManager.getPrimaryClip().getItemAt(0).getText());
			});
		/*
		if (BuildConfig.FLAVOR.equals(FLAVOR_FULL))
			MobileAds.initialize(this, ADMOB_APP_ID);
		*/
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Notification buildForegroundNotification() {
		boolean isOreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
		if (isOreo)
			createChannelForOreo();

		Intent MainActivityStarter = new Intent(this, MainActivity.class);
		PendingIntent notificationAction = PendingIntent.getActivity(this, 0, MainActivityStarter, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, isOreo ? CHANNEL_ID : "");
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

		NotificationChannel channel = new NotificationChannel(title, title, NotificationManager.IMPORTANCE_DEFAULT);
		channel.setDescription(description);
		notificationManager.createNotificationChannel(channel);
	}
//===================================================

	private void initBubble() {
		bubble = new ImageView(this);
		bubble.setImageResource(R.drawable.ic_bubble);
	}
//===================================================

	private void handleNewQuery(CharSequence query) {
		try {
			new URL(query.toString()); //ignore if url.
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
