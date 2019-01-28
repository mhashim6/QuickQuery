package mhashim6.android.quickQuery

import android.app.Application
import mhashim6.android.quickQuery.Utils.IS_OREO

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (IS_OREO)
            createNotificationsChannel(this)
    }

}