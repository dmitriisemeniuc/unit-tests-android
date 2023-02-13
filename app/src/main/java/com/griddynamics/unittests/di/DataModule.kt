package com.griddynamics.unittests.di

import android.content.Context
import com.griddynamics.unittests.data.db.dao.CommitsDao
import com.griddynamics.unittests.data.source.local.ReposLocalDataSource
import com.griddynamics.unittests.data.source.local.ReposLocalDataSourceImpl
import com.griddynamics.unittests.data.db.dao.ReposDao
import com.griddynamics.unittests.data.repository.CommitsRepositoryImpl
import com.griddynamics.unittests.data.source.remote.ReposRemoteDataSource
import com.griddynamics.unittests.data.repository.ReposRepositoryImpl
import com.griddynamics.unittests.data.source.local.CommitsLocalDataSource
import com.griddynamics.unittests.data.source.local.CommitsLocalDataSourceImpl
import com.griddynamics.unittests.data.source.mapper.CommitMapper
import com.griddynamics.unittests.data.source.mapper.RepoMapper
import com.griddynamics.unittests.data.source.remote.CommitsRemoteDataSource
import com.griddynamics.unittests.domain.repository.CommitsRepository
import com.griddynamics.unittests.domain.repository.ReposRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module
class DataModule {

    @Provides
    fun providesReposLocalDataSource(reposDao: ReposDao): ReposLocalDataSource {
        return ReposLocalDataSourceImpl(reposDao = reposDao)
    }

    @Provides
    fun providesRepoMapper(): RepoMapper {
        return RepoMapper()
    }

    @Provides
    fun provideReposRepository(
        context: Context,
        reposRemoteDataSource: ReposRemoteDataSource,
        reposLocalDataSource: ReposLocalDataSource,
        reposMapper: RepoMapper
    ): ReposRepository {
        return ReposRepositoryImpl(
            context = context,
            reposRemoteDataSource = reposRemoteDataSource,
            reposLocalDataSource = reposLocalDataSource,
            reposMapper = reposMapper,
            dispatcher = Dispatchers.IO
        )
    }

    @Provides
    fun providesCommitsLocalDataSource(commitDao: CommitsDao): CommitsLocalDataSource {
        return CommitsLocalDataSourceImpl(commitDao = commitDao)
    }

    @Provides
    fun providesCommitMapper(): CommitMapper {
        return CommitMapper()
    }

    @Provides
    fun provideCommitsRepository(
        context: Context,
        commitsRemoteDataSource: CommitsRemoteDataSource,
        commitsLocalDataSource: CommitsLocalDataSource,
        commitMapper: CommitMapper
    ): CommitsRepository {
        return CommitsRepositoryImpl(
            context = context,
            commitsRemoteDataSource = commitsRemoteDataSource,
            commitsLocalDataSource = commitsLocalDataSource,
            commitMapper = commitMapper,
            dispatcher = Dispatchers.IO
        )
    }
}