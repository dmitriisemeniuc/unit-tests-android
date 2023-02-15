package com.dmitriisemeniuc.unittests.presentation.search.viewmodel

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import com.dmitriisemeniuc.unittests.common.net.NetworkFailure
import com.dmitriisemeniuc.unittests.domain.usecase.GetReposByUserUseCase
import com.dmitriisemeniuc.unittests.common.net.Result
import com.dmitriisemeniuc.unittests.data.api.GitHubApi
import com.dmitriisemeniuc.unittests.data.api.model.response.RepoResponse
import com.dmitriisemeniuc.unittests.data.db.dao.ReposDao
import com.dmitriisemeniuc.unittests.data.db.entities.RepoEntity
import com.dmitriisemeniuc.unittests.data.repository.ReposRepositoryImpl
import com.dmitriisemeniuc.unittests.data.source.local.ReposLocalDataSource
import com.dmitriisemeniuc.unittests.data.source.local.ReposLocalDataSourceImpl
import com.dmitriisemeniuc.unittests.data.source.mapper.RepoMapper
import com.dmitriisemeniuc.unittests.data.source.remote.ReposRemoteDataSource
import com.dmitriisemeniuc.unittests.data.source.remote.ReposRemoteDataSourceImpl
import com.dmitriisemeniuc.unittests.domain.model.Repo
import com.dmitriisemeniuc.unittests.helper.captureValues
import com.dmitriisemeniuc.unittests.helper.getOrAwaitValue
import com.dmitriisemeniuc.unittests.presentation.extensions.isNetworkAvailable
import com.dmitriisemeniuc.unittests.presentation.util.CacheTimeLimiter
import com.dmitriisemeniuc.unittests.util.TestUtil
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchReposViewModelTest {

    companion object {
        const val USER = "test_user"
    }

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testScope = TestScope()
    private val testDispatcher = UnconfinedTestDispatcher(testScope.testScheduler)

    private val mockedContext = mockk<Context>(relaxed = true)
    private val mockedApplication = mockk<Application>(relaxed = true)
    private var mockedReposApi = mockk<GitHubApi>()
    private var mockedReposDao = mockk<ReposDao>()
    private var reposMapper = RepoMapper()
    private var reposRepository = mockk<ReposRepositoryImpl>()
    private var mockedRepoListCacheTimeLimiter = mockk<CacheTimeLimiter<String>>()

    private var savedStateHandle = SavedStateHandle()
    private lateinit var getReposByUserUseCase: GetReposByUserUseCase
    private lateinit var reposRemoteDataSource: ReposRemoteDataSource
    private lateinit var reposLocalDataSource: ReposLocalDataSource
    private lateinit var mockedApiResponse: List<RepoResponse>
    private lateinit var mockedDbResponse: List<RepoEntity>
    private lateinit var mockedRepos: List<Repo>

    // Class under test
    private lateinit var viewModel: SearchReposViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        reposRemoteDataSource = ReposRemoteDataSourceImpl(mockedReposApi)
        reposLocalDataSource = ReposLocalDataSourceImpl(mockedReposDao)
        reposRepository = ReposRepositoryImpl(
            context = mockedContext,
            reposRemoteDataSource = reposRemoteDataSource,
            reposLocalDataSource = reposLocalDataSource,
            reposMapper = reposMapper,
            dispatcher = testDispatcher,
            cacheTimeLimiter = mockedRepoListCacheTimeLimiter
        )
        getReposByUserUseCase = GetReposByUserUseCase(reposRepository)
        viewModel = SearchReposViewModel(
            application = mockedApplication,
            getReposByUserUseCase = getReposByUserUseCase,
            savedStateHandle = savedStateHandle
        )
        mockedApiResponse = TestUtil.generateReposApiResponse(USER)
        mockedDbResponse = TestUtil.generateReposDbModels(USER)
        mockedRepos = TestUtil.generateRepoDomainModels(USER)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search From Network Loading Is Shown Before Content Test`() = testScope.runTest {
        // given
        coEvery { mockedReposDao.findByUser(USER) } returns emptyList()
        // when
        viewModel.search(USER)
        // then
        val actual = viewModel.repositories.getOrAwaitValue()
        assert(actual is Result.Loading)
    }

    @Test
    fun `search From Database Success Test`() = testScope.runTest {
        // given
        coEvery { mockedReposDao.findByUser(USER) } returns mockedDbResponse
        coEvery { mockedRepoListCacheTimeLimiter.shouldFetch(key = any()) } returns false
        // when
        viewModel.search(USER)
        // then
        val expected = mockedRepos
        viewModel.repositories.captureValues {
            assertThat(values.size, `is`(1))
            assert(values.single() is Result.Success)
            assertThat(expected, `is`((values.single() as Result.Success<List<Repo>>).data))
        }
    }

    @Test
    fun `search From Network NetworkFailure Error Test`() = testScope.runTest {
        // given
        coEvery { mockedReposDao.findByUser(USER) } returns emptyList()
        coEvery { mockedRepoListCacheTimeLimiter.shouldFetch(key = any()) } returns true
        coEvery { mockedRepoListCacheTimeLimiter.reset(key = any()) } just Runs
        every { mockedContext.isNetworkAvailable() } returns false
        // when
        viewModel.search(USER)
        // then
        viewModel.repositories.captureValues {
            assertThat(values.size, `is`(2))
            assert(values.first() is Result.Loading)
            assert(values.last() is Result.Error)
            assert((values.last() as Result.Error).error is NetworkFailure)
        }
    }

    @Test
    fun `search From Network Success Test`() = testScope.runTest {
        // given
        coEvery { mockedReposApi.getReposByUser(user = any()) } returns mockedApiResponse
        coEvery { mockedReposDao.findByUser(user = any()) } returns mockedDbResponse
        coEvery { mockedReposDao.insertAll(repos = any()) } just Runs
        coEvery { mockedRepoListCacheTimeLimiter.shouldFetch(key = any()) } returns true
        coEvery { mockedRepoListCacheTimeLimiter.reset(key = any()) } just Runs
        every { mockedContext.isNetworkAvailable() } returns true
        // when
        viewModel.search(USER)
        // then
        val expected = mockedRepos
        viewModel.repositories.captureValues {
            assertThat(values.size, `is`(2))
            assert(values.first() is Result.Loading)
            assert(values.last() is Result.Success)
            assertThat(expected, `is`((values.last() as Result.Success<List<Repo>>).data))
        }
    }
}