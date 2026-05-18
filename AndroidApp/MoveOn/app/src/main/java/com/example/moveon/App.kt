package com.example.moveon

import android.app.Application
import com.example.moveon.data.TokenStorage
import org.osmdroid.config.Configuration

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenStorage.init(this)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osm_pref", MODE_PRIVATE)
        )

        Configuration.getInstance().userAgentValue = packageName
    }
}
