package mhashim6.android.quickQuery

import android.annotation.SuppressLint
import android.app.Application

class App : Application() {

    @SuppressLint("NewApi")
    override fun onCreate() {
        super.onCreate()

        if (IS_OREO)
            createNotificationsChannel(this)
    }

}