package com.dmitriisemeniuc.unittests.data.repository

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dmitriisemeniuc.unittests.common.net.ApiResponse
import com.dmitriisemeniuc.unittests.common.net.NetworkFailure
import com.dmitriisemeniuc.unittests.common.net.NotFoundException
import com.dmitriisemeniuc.unittests.data.source.local.ReposLocalDataSource
import com.dmitriisemeniuc.unittests.data.source.remote.ReposRemoteDataSource
import com.dmitriisemeniuc.unittests.data.source.mapper.RepoMapper
import com.dmitriisemeniuc.unittests.domain.model.Repo
import com.dmitriisemeniuc.unittests.domain.repository.ReposRepository
import com.dmitriisemeniuc.unittests.common.net.Result
import com.dmitriisemeniuc.unittests.presentation.extensions.isNetworkAvailable
import com.dmitriisemeniuc.unittests.presentation.util.CacheTimeLimiter
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.TimeUnit

class ReposRepositoryImpl(
    private val context: Context,
    private val reposRemoteDataSource: ReposRemoteDataSource,
    private val reposLocalDataSource: ReposLocalDataSource,
    private val reposMapper: RepoMapper,
    private val cacheTimeLimiter: CacheTimeLimiter<String> = CacheTimeLimiter(
        10,
        TimeUnit.MINUTES
    ),
    private val dispatcher: CoroutineDispatcher
) : ReposRepository {

    override fun getReposByUser(user: String): LiveData<Result<List<Repo>>> = liveData(dispatcher) {
        val localData = loadDataFromDb(user)
        if (shouldFetch(user = user, data = localData)) {
            emit(Result.Loading())
            when (val apiResponse = loadDataFromNetwork(user)) {
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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun loadDataFromDb(user: String): List<Repo> {
        val repos = reposLocalDataSource.getReposByUser(user)
        return repos.map {
            reposMapper.mapStorageToDomain(it)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun shouldFetch(user: String, data: List<Repo>?): Boolean {
        return data == null || data.isEmpty() || cacheTimeLimiter.shouldFetch(user)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun loadDataFromNetwork(user: String): ApiResponse<List<Repo>> {
        if (!isOnline()) {
            return ApiResponse.Error(NetworkFailure())
        }

        return runCatching {
            reposRemoteDataSource.getReposByUser(user)
        }.mapCatching { response ->
            if (response.isEmpty()) {
                throw NotFoundException()
            }
            val repos = response.map {
                reposMapper.mapApiToDomain(it)
            }
            ApiResponse.Success(repos)
        }.getOrElse {
            ApiResponse.Error(it)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun saveData(data: List<Repo>) {
        val repos = data.map {
            reposMapper.mapDomainToStorage(it)
        }
        reposLocalDataSource.saveRepos(repos)
    }

    private fun onFetchFailed(user: String) {
        cacheTimeLimiter.reset(user)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun isOnline() = context.isNetworkAvailable()
}