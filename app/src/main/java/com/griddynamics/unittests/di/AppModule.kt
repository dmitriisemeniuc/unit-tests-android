package com.griddynamics.unittests.di

import android.app.Application
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val application: Application) {

    @Provides
    fun providesApplication(): Application {
        return application
    }
}