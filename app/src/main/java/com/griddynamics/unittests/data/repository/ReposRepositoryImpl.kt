package com.griddynamics.unittests.data.repository

import android.app.Application
import com.griddynamics.unittests.common.net.ApiResponse
import com.griddynamics.unittests.common.net.NetworkFailure
import com.griddynamics.unittests.common.net.NotFoundException
import com.griddynamics.unittests.data.source.local.ReposLocalDataSource
import com.griddynamics.unittests.data.source.remote.ReposRemoteDataSource
import com.griddynamics.unittests.data.source.mapper.ReposMapper
import com.griddynamics.unittests.domain.model.Repo
import com.griddynamics.unittests.domain.repository.ReposRepository
import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.presentation.extensions.isNetworkAvailable
import com.griddynamics.unittests.presentation.util.CacheTimeLimiter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

class ReposRepositoryImpl(
    private val application: Application,
    private val reposRemoteDataSource: ReposRemoteDataSource,
    private val reposLocalDataSource: ReposLocalDataSource,
    private val reposMapper: ReposMapper
) : ReposRepository {

    private val repoListCacheTimeLimiter = CacheTimeLimiter<String>(10, TimeUnit.MINUTES)

    override fun getReposByUser(user: String): Flow<Result<List<Repo>>> = flow {
        val localData = loadDataFromDb(user)
        if (shouldFetch(user = user, data = localData)) {
            emit(Result.Loading())
            when(val apiResponse = loadDataFromNetwork(user)) {
                is ApiResponse.Success -> {
                    saveData(apiResponse.data)
                    val freshData = loadDataFromDb(user)
                    emit(Result.Success(freshData))
                }
                is ApiResponse.Error -> {
                    onFetchFailed(user)
                    emit(Result.Error(apiResponse.reason))
                }
            }
        } else {
            emit(Result.Success(localData))
        }
    }

    private suspend fun loadDataFromDb(user: String): List<Repo> {
        return reposLocalDataSource.getReposByUser(user).map {
            reposMapper.mapToDomain(it)
        }
    }

    private fun shouldFetch(user: String, data: List<Repo>?): Boolean {
        return data == null || data.isEmpty() || repoListCacheTimeLimiter.shouldFetch(user)
    }

    private suspend fun loadDataFromNetwork(user: String): ApiResponse<List<Repo>> {
        return if (isOnline()) {
            runCatching {
                reposRemoteDataSource.getReposByUser(user)
            }.mapCatching { response ->
                when {
                    response.isEmpty() -> {
                        throw NotFoundException()
                    }
                    else -> {
                        ApiResponse.Success(response.map {
                            reposMapper.mapToDomain(it)
                        })
                    }
                }
            }.getOrElse {
                ApiResponse.Error(it)
            }
        } else {
            ApiResponse.Error(NetworkFailure())
        }
    }

    private suspend fun saveData(data: List<Repo>) {
        reposLocalDataSource.saveRepos(data.map {
            reposMapper.mapToStorage(it)
        })
    }

    private fun onFetchFailed(user: String) {
        repoListCacheTimeLimiter.reset(user)
    }

    private fun isOnline() = application.isNetworkAvailable()
}