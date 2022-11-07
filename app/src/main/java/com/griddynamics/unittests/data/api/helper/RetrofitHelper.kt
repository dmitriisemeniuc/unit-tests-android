package com.griddynamics.unittests.data.api.helper

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitHelper {

    fun <T> create(
        endpoint: Class<T>,
        okHttpClient: OkHttpClient,
        baseUrl: String,
    ): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(KotlinConverterFactoryHelper.provideKotlinSerialization())
            .build()
            .create(endpoint)
    }
}