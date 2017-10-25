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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by mhashim6 on 25/10/2017.
 */

public class ClipboardMonitor extends Service {
	public static final String QUICK_QUERY_ACTION = "QUICK_QUERY_ACTION";


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
	}


	public void showBubble(CharSequence query) {
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		if (windowManager != null) {//TODO

			ImageView bubble = new ImageView(this);
			bubble.setImageResource(R.mipmap.ic_launcher_round);
			bubble.setOnClickListener(view -> {
				windowManager.removeView(bubble);
				startQQActivity(query);
			});

			bubble.setOnLongClickListener(view -> {
				windowManager.removeView(bubble);
				return true;
			});

			WindowManager.LayoutParams params = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT);

			params.type = WindowManager.LayoutParams.TYPE_TOAST;
			params.gravity = Gravity.TOP | Gravity.LEFT;
			params.x = 0;
			params.y = 100;

			windowManager.addView(bubble, params);
		}
	}

	private void startQQActivity(CharSequence query) {
		Intent qqStarter = new Intent(this, QQActivity.class);
		qqStarter.setAction(QUICK_QUERY_ACTION);
		qqStarter.putExtra(Intent.EXTRA_TEXT, query);
		qqStarter.setFlags(FLAG_ACTIVITY_NEW_TASK);
		startActivity(qqStarter);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
