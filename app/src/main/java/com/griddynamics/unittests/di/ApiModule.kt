package com.griddynamics.unittests.di

import com.griddynamics.unittests.data.source.remote.ReposRemoteDataSource
import com.griddynamics.unittests.data.source.remote.ReposRemoteDataSourceImpl
import com.griddynamics.unittests.data.api.GitHubApi
import com.griddynamics.unittests.data.api.helper.RetrofitHelper
import com.griddynamics.unittests.data.source.remote.CommitsRemoteDataSourceImpl
import com.griddynamics.unittests.data.source.remote.CommitsRemoteDataSource
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
class ApiModule {

    @Provides
    fun providesOktHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    fun providesReposApi(okHttpClient: OkHttpClient): GitHubApi {
        return RetrofitHelper.create(
            endpoint = GitHubApi::class.java,
            okHttpClient = okHttpClient,
            baseUrl = GitHubApi.BASE_URL
        )
    }

    @Provides
    fun providesReposRemoteDataSource(api: GitHubApi): ReposRemoteDataSource {
        return ReposRemoteDataSourceImpl(api)
    }

    @Provides
    fun providesRepoCommitsRemoteDataSource(api: GitHubApi): CommitsRemoteDataSource {
        return CommitsRemoteDataSourceImpl(api)
    }
}