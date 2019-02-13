package mhashim6.android.quickQuery

import android.annotation.SuppressLint
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.PixelFormat
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URL


/**
 * Created by mhashim6 on 25/10/2017.
 */

class ClipboardMonitor : Service() {

    private lateinit var bubble: ImageView

    private var query: CharSequence? = null

    @SuppressLint("NewApi")
    override fun onCreate() {

        super.onCreate()

        if (IS_OREO)
            startForeground(FOREGROUND_ID, buildForegroundNotification(this))
        else
            Log.i("QuickQuery", "not oreo, skipping notification.")

        initBubble()

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.addPrimaryClipChangedListener { handleNewQuery(clipboardManager.primaryClip?.getItemAt(0)?.text) }
    }

    private fun initBubble() {
        bubble = ImageView(this)
        bubble.setImageResource(R.drawable.ic_bubble)
    }

    private fun initAndAddNewBubbleToWindow() {
        initBubble()

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(bubble, LAYOUT_PARAMS)
    }

    private fun handleNewQuery(query: CharSequence?) {
        query?.let {
            try {
                URL(it.toString()) //ignore if url.
            } catch (e: MalformedURLException) {
                this.query = it
                mainScope.launch {
                    showNewBubble()
                }
            }
        }
    }

    private fun showNewBubble() {
        bubble.visibility = View.GONE

        initAndAddNewBubbleToWindow()
        val newBubble = this.bubble

        newBubble.setOnClickListener {
            newBubble.visibility = View.GONE
            startQQActivity(query)
        }
        newBubble.setOnLongClickListener {
            newBubble.visibility = View.GONE
            true
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val duration = (preferences.getString("duration", "6")).toLong() * 1000
        newBubble.postDelayed({ removeBubbleFromWindow(newBubble) }, duration)
    }

    private fun removeBubbleFromWindow(bubble: View) {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.removeView(bubble)
    }

    private fun startQQActivity(query: CharSequence?) {
        val qqStarter = Intent(this, QQActivity::class.java)
        qqStarter.action = QUICK_QUERY_ACTION
        qqStarter.putExtra(Intent.EXTRA_TEXT, query)
        qqStarter.flags = FLAG_ACTIVITY_NEW_TASK
        startActivity(qqStarter)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private const val FOREGROUND_ID = 77
        private val LAYOUT_PARAMS = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (IS_OREO)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT)

        init {
            LAYOUT_PARAMS.gravity = Gravity.TOP or Gravity.START
            LAYOUT_PARAMS.x = 3
            LAYOUT_PARAMS.y = 180
        }
    }

}
