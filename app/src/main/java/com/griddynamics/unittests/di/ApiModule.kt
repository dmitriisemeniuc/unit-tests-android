package com.griddynamics.unittests.di

import com.griddynamics.unittests.data.source.remote.ReposRemoteDataSource
import com.griddynamics.unittests.data.source.remote.ReposRemoteDataSourceImpl
import com.griddynamics.unittests.data.api.ReposApi
import com.griddynamics.unittests.data.api.helper.RetrofitHelper
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
    fun providesReposApi(okHttpClient: OkHttpClient): ReposApi {
        return RetrofitHelper.create(
            endpoint = ReposApi::class.java,
            okHttpClient = okHttpClient,
            baseUrl = ReposApi.ENDPOINT
        )
    }

    @Provides
    fun providesReposRemoteDataSource(api: ReposApi): ReposRemoteDataSource {
        return ReposRemoteDataSourceImpl(api)
    }
}