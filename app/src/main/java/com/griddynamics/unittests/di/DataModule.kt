package com.griddynamics.unittests.di

import android.content.Context
import com.griddynamics.unittests.data.db.dao.RepoCommitDao
import com.griddynamics.unittests.data.source.local.ReposLocalDataSource
import com.griddynamics.unittests.data.source.local.ReposLocalDataSourceImpl
import com.griddynamics.unittests.data.db.dao.ReposDao
import com.griddynamics.unittests.data.repository.RepoCommitsRepositoryImpl
import com.griddynamics.unittests.data.source.remote.ReposRemoteDataSource
import com.griddynamics.unittests.data.repository.ReposRepositoryImpl
import com.griddynamics.unittests.data.source.local.RepoCommitsLocalDataSource
import com.griddynamics.unittests.data.source.local.RepoCommitsLocalDataSourceImpl
import com.griddynamics.unittests.data.source.mapper.RepoCommitMapper
import com.griddynamics.unittests.data.source.mapper.ReposMapper
import com.griddynamics.unittests.data.source.remote.RepoCommitsRemoteDataSource
import com.griddynamics.unittests.domain.repository.RepoCommitsRepository
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
    fun providesReposMapper(): ReposMapper {
        return ReposMapper()
    }

    @Provides
    fun provideReposRepository(
        context: Context,
        reposRemoteDataSource: ReposRemoteDataSource,
        reposLocalDataSource: ReposLocalDataSource,
        reposMapper: ReposMapper
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
    fun providesRepoCommitsLocalDataSource(repoCommitDao: RepoCommitDao): RepoCommitsLocalDataSource {
        return RepoCommitsLocalDataSourceImpl(repoCommitDao = repoCommitDao)
    }

    @Provides
    fun providesRepoCommitMapper(): RepoCommitMapper {
        return RepoCommitMapper()
    }

    @Provides
    fun provideRepoCommitsRepository(
        context: Context,
        repoCommitsRemoteDataSource: RepoCommitsRemoteDataSource,
        repoCommitsLocalDataSource: RepoCommitsLocalDataSource,
        repoCommitsMapper: RepoCommitMapper
    ): RepoCommitsRepository {
        return RepoCommitsRepositoryImpl(
            context = context,
            repoCommitsRemoteDataSource = repoCommitsRemoteDataSource,
            repoCommitsLocalDataSource = repoCommitsLocalDataSource,
            repoCommitsMapper = repoCommitsMapper,
            dispatcher = Dispatchers.IO
        )
    }
}