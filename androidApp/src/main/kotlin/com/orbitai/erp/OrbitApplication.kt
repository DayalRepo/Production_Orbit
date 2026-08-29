package com.orbitai.erp

import android.app.Application
import com.orbitai.erp.di.initKoin

class OrbitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
