package com.dmitriisemeniuc.unittests.app

import android.app.Application
import com.dmitriisemeniuc.unittests.di.AppComponent
import com.dmitriisemeniuc.unittests.di.AppModule
import com.dmitriisemeniuc.unittests.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(application = this)).build()
    }
}