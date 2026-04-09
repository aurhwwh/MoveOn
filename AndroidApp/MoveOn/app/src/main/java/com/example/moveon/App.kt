package com.example.moveon

import android.app.Application
import com.example.moveon.data.TokenStorage

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenStorage.init(this)
    }
}