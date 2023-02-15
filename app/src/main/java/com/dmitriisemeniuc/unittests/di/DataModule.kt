package com.dmitriisemeniuc.unittests.di

import android.content.Context
import com.dmitriisemeniuc.unittests.data.db.dao.CommitsDao
import com.dmitriisemeniuc.unittests.data.source.local.CommitsLocalDataSource
import com.dmitriisemeniuc.unittests.data.source.local.ReposLocalDataSourceImpl
import com.dmitriisemeniuc.unittests.data.db.dao.ReposDao
import com.dmitriisemeniuc.unittests.data.repository.CommitsRepositoryImpl
import com.dmitriisemeniuc.unittests.data.source.remote.ReposRemoteDataSource
import com.dmitriisemeniuc.unittests.data.repository.ReposRepositoryImpl
import com.dmitriisemeniuc.unittests.data.source.local.CommitsLocalDataSourceImpl
import com.dmitriisemeniuc.unittests.data.source.local.ReposLocalDataSource
import com.dmitriisemeniuc.unittests.data.source.mapper.CommitMapper
import com.dmitriisemeniuc.unittests.data.source.mapper.RepoMapper
import com.dmitriisemeniuc.unittests.data.source.remote.CommitsRemoteDataSource
import com.dmitriisemeniuc.unittests.domain.repository.CommitsRepository
import com.dmitriisemeniuc.unittests.domain.repository.ReposRepository
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