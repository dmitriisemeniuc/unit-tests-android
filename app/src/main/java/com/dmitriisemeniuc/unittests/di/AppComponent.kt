package com.dmitriisemeniuc.unittests.di

import com.dmitriisemeniuc.unittests.presentation.commits.CommitsFragment
import com.dmitriisemeniuc.unittests.presentation.search.SearchReposFragment
import dagger.Component

@Component(modules = [AppModule::class, DatabaseModule::class, DaoModule::class,
    ApiModule::class, DomainModule::class, DataModule::class])
interface AppComponent {
    fun inject(searchFragment: SearchReposFragment)
    fun inject(repoCommitsFragment: CommitsFragment)
}