package com.griddynamics.unittests.di

import android.app.Application
import com.griddynamics.unittests.data.source.local.ReposLocalDataSource
import com.griddynamics.unittests.data.source.local.ReposLocalDataSourceImpl
import com.griddynamics.unittests.data.db.dao.ReposDao
import com.griddynamics.unittests.data.source.remote.ReposRemoteDataSource
import com.griddynamics.unittests.data.repository.ReposRepositoryImpl
import com.griddynamics.unittests.data.source.mapper.ReposMapper
import com.griddynamics.unittests.domain.repository.ReposRepository
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @Provides
    fun providesReposLocalDataSource(reposDao: ReposDao): ReposLocalDataSource {
        return ReposLocalDataSourceImpl(reposDao = reposDao)
    }

    @Provides
    fun providesReposMapper(): ReposMapper {
        return ReposMapper()
    }

    @Provides
    fun provideReposRepository(
        application: Application,
        reposRemoteDataSource: ReposRemoteDataSource,
        reposLocalDataSource: ReposLocalDataSource,
        reposMapper: ReposMapper
    ): ReposRepository {
        return ReposRepositoryImpl(
            application = application,
            reposRemoteDataSource = reposRemoteDataSource,
            reposLocalDataSource = reposLocalDataSource,
            reposMapper = reposMapper
        )
    }
}