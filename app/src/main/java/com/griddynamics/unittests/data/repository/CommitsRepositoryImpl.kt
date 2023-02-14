package com.griddynamics.unittests.data.repository

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.griddynamics.unittests.common.net.ApiResponse
import com.griddynamics.unittests.common.net.NetworkFailure
import com.griddynamics.unittests.common.net.NotFoundException
import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.data.api.model.request.CommitsParams
import com.griddynamics.unittests.data.source.local.CommitsLocalDataSource
import com.griddynamics.unittests.data.source.mapper.CommitMapper
import com.griddynamics.unittests.data.source.remote.CommitsRemoteDataSource
import com.griddynamics.unittests.domain.model.Commit
import com.griddynamics.unittests.domain.repository.CommitsRepository
import com.griddynamics.unittests.presentation.extensions.isNetworkAvailable
import com.griddynamics.unittests.presentation.util.CacheTimeLimiter
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.TimeUnit

class CommitsRepositoryImpl(
    private val context: Context,
    private val commitsRemoteDataSource: CommitsRemoteDataSource,
    private val commitsLocalDataSource: CommitsLocalDataSource,
    private val commitMapper: CommitMapper,
    private val cacheTimeLimiter: CacheTimeLimiter<Long> = CacheTimeLimiter(
        10,
        TimeUnit.MINUTES
    ),
    private val dispatcher: CoroutineDispatcher
) : CommitsRepository {

    companion object {
        const val USER = "test_user"
        const val REPO_ID = 100L
        const val REPO = "test_repo"
    }

    override fun getCommitsByOwnerAndRepo(
        params: CommitsParams
    ): LiveData<Result<List<Commit>>> = liveData(dispatcher) {
        val localData = loadDataFromDb(params.repoId)
        if (shouldFetch(repoId = params.repoId, data = localData)) {
            emit(Result.Loading())
            when (val apiResponse = loadDataFromNetwork(params)) {
                is ApiResponse.Success -> {
                    saveData(apiResponse.data)
                    val freshData = loadDataFromDb(params.repoId)
                    emit(Result.Success(freshData))
                }
                is ApiResponse.Error -> {
                    onFetchFailed(params.repoId)
                    emit(Result.Error(apiResponse.reason))
                }
            }
        } else {
            emit(Result.Success(localData))
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun loadDataFromDb(repoId: Long): List<Commit> {
        val commits = commitsLocalDataSource.getCommitsByRepoId(repoId)
        return commits.map {
            commitMapper.mapStorageToDomain(it)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun shouldFetch(repoId: Long, data: List<Commit>?): Boolean {
        return data == null || data.isEmpty() || cacheTimeLimiter.shouldFetch(repoId)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun loadDataFromNetwork(params: CommitsParams): ApiResponse<List<Commit>> {
        if (!isOnline()) {
            return ApiResponse.Error(NetworkFailure())
        }

        return runCatching {
            commitsRemoteDataSource.getCommitsByUserAndRepo(params.user, params.repo)
        }.mapCatching { responseList ->
            if (responseList.isEmpty()) {
                throw NotFoundException()
            }
            val commits = responseList.map {
                commitMapper.mapApiToDomain(it, params.repoId)
            }
            ApiResponse.Success(commits)
        }.getOrElse {
            ApiResponse.Error(it)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun saveData(data: List<Commit>) {
        val commits = data.map {
            commitMapper.mapDomainToStorage(it)
        }
        commitsLocalDataSource.saveCommits(commits)
    }

    private fun onFetchFailed(repoId: Long) {
        cacheTimeLimiter.reset(repoId)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun isOnline() = context.isNetworkAvailable()
}