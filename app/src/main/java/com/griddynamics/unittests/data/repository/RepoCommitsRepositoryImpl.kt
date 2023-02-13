package com.griddynamics.unittests.data.repository

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.griddynamics.unittests.common.net.ApiResponse
import com.griddynamics.unittests.common.net.NetworkFailure
import com.griddynamics.unittests.common.net.NotFoundException
import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.data.api.model.request.RepoCommitsParams
import com.griddynamics.unittests.data.source.local.RepoCommitsLocalDataSource
import com.griddynamics.unittests.data.source.mapper.RepoCommitMapper
import com.griddynamics.unittests.data.source.remote.RepoCommitsRemoteDataSource
import com.griddynamics.unittests.domain.model.RepoCommit
import com.griddynamics.unittests.domain.repository.RepoCommitsRepository
import com.griddynamics.unittests.presentation.extensions.isNetworkAvailable
import com.griddynamics.unittests.presentation.util.CacheTimeLimiter
import kotlinx.coroutines.CoroutineDispatcher
import java.util.concurrent.TimeUnit

class RepoCommitsRepositoryImpl(
    private val context: Context,
    private val repoCommitsRemoteDataSource: RepoCommitsRemoteDataSource,
    private val repoCommitsLocalDataSource: RepoCommitsLocalDataSource,
    private val repoCommitsMapper: RepoCommitMapper,
    private val reposCommitsCacheTimeLimiter: CacheTimeLimiter<Long> = CacheTimeLimiter(
        10,
        TimeUnit.MINUTES
    ),
    private val dispatcher: CoroutineDispatcher
) : RepoCommitsRepository {

    override fun getCommitsByOwnerAndRepo(
        params: RepoCommitsParams
    ): LiveData<Result<List<RepoCommit>>> = liveData(dispatcher) {
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
    suspend fun loadDataFromDb(repoId: Long): List<RepoCommit> {
        val commits = repoCommitsLocalDataSource.getRepoCommitsByRepoId(repoId)
        return commits.map {
            repoCommitsMapper.mapStorageToDomain(it)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun shouldFetch(repoId: Long, data: List<RepoCommit>?): Boolean {
        return data == null || data.isEmpty() || reposCommitsCacheTimeLimiter.shouldFetch(repoId)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun loadDataFromNetwork(params: RepoCommitsParams): ApiResponse<List<RepoCommit>> {
        if (!isOnline()) {
            return ApiResponse.Error(NetworkFailure())
        }

        return runCatching {
            repoCommitsRemoteDataSource.getRepoCommitsByUserAndRepo(params.user, params.repo)
        }.mapCatching { responseList ->
            if (responseList.isEmpty()) {
                throw NotFoundException()
            }
            val commits = responseList.map {
                repoCommitsMapper.mapApiToDomain(it, params.repoId)
            }
            ApiResponse.Success(commits)
        }.getOrElse {
            ApiResponse.Error(it)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun saveData(data: List<RepoCommit>) {
        val commits = data.map {
            repoCommitsMapper.mapDomainToStorage(it)
        }
        repoCommitsLocalDataSource.saveRepoCommits(commits)
    }

    private fun onFetchFailed(repoId: Long) {
        reposCommitsCacheTimeLimiter.reset(repoId)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun isOnline() = context.isNetworkAvailable()
}