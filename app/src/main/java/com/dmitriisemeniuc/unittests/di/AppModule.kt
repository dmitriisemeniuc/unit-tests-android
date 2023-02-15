package com.dmitriisemeniuc.unittests.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val application: Application) {

    @Provides
    fun providesApplication(): Application {
        return application
    }

    @Provides
    fun providesContext(): Context {
        return application.applicationContext
    }
}