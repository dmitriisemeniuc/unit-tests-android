package com.griddynamics.unittests.di

import com.griddynamics.unittests.presentation.commits.CommitsFragment
import com.griddynamics.unittests.presentation.search.SearchReposFragment
import dagger.Component

@Component(modules = [AppModule::class, DatabaseModule::class, DaoModule::class,
    ApiModule::class, DomainModule::class, DataModule::class])
interface AppComponent {
    fun inject(searchFragment: SearchReposFragment)
    fun inject(repoCommitsFragment: CommitsFragment)
}