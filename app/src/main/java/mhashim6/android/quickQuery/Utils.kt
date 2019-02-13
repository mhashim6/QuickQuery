package mhashim6.android.quickQuery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Created by mhashim6 on 29/10/2017.
 */

const val QUICK_QUERY_ACTION = "QUICK_QUERY_ACTION"
const val GOOGLE_PLAY_LINK_PRO = "market://details?id=mhashim6.android.quickQuery.full"
const val DONATE_LINK = "http://paypal.com/paypalme/mhashim6"
const val COPY_KEY = "copy_key"
val IS_OREO = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

val mainScope = CoroutineScope(Dispatchers.Main)

fun openWebPage(context: Context, url: String) {
    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(webIntent)
}
