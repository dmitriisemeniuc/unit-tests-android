package com.dmitriisemeniuc.unittests.di

import com.dmitriisemeniuc.unittests.domain.repository.CommitsRepository
import com.dmitriisemeniuc.unittests.domain.repository.ReposRepository
import com.dmitriisemeniuc.unittests.domain.usecase.GetCommitsUseCase
import com.dmitriisemeniuc.unittests.domain.usecase.GetReposByUserUseCase
import dagger.Module
import dagger.Provides

@Module
class DomainModule {

    @Provides
    fun provideGetReposByUserUseCase(reposRepository: ReposRepository): GetReposByUserUseCase {
        return GetReposByUserUseCase(reposRepository = reposRepository)
    }

    @Provides
    fun provideGetCommitsUseCase(commitsRepository: CommitsRepository): GetCommitsUseCase {
        return GetCommitsUseCase(commitsRepository = commitsRepository)
    }
}