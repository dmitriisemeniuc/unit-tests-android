package com.griddynamics.unittests.di

import android.app.Application
import com.griddynamics.unittests.domain.usecase.GetReposByUserUseCase
import com.griddynamics.unittests.presentation.search.viewmodel.SearchReposViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val application: Application) {

    @Provides
    fun providesApplication(): Application {
        return application
    }

    @Provides
    fun providesSearchReposViewModelFactory(
        getReposByUserUseCase: GetReposByUserUseCase,
        application: Application
    ): SearchReposViewModelFactory {
        return SearchReposViewModelFactory(getReposByUserUseCase = getReposByUserUseCase,
        application = application)
    }
}