package com.griddynamics.unittests.di

import com.griddynamics.unittests.presentation.search.SearchReposActivity
import dagger.Component

@Component(modules = [AppModule::class, DatabaseModule::class, DaoModule::class,
    ApiModule::class, DomainModule::class, DataModule::class])
interface AppComponent {
    fun inject(searchReposActivity: SearchReposActivity)
}