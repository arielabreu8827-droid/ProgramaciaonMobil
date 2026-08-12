package com.itespf.aulamobile

import android.app.Application
import com.itespf.aulamobile.data.util.ServiceLocator

class AulaMobileApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}
