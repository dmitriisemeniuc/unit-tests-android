package com.griddynamics.unittests.app

import android.app.Application
import com.griddynamics.unittests.di.AppComponent
import com.griddynamics.unittests.di.AppModule
import com.griddynamics.unittests.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(application = this)).build()
    }
}