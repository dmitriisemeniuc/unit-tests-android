package com.griddynamics.unittests.di

import com.griddynamics.unittests.data.db.AppDatabase
import com.griddynamics.unittests.data.db.dao.ReposDao
import dagger.Module
import dagger.Provides

@Module
class DaoModule {

    @Provides
    fun providesReposDao(appDatabase: AppDatabase): ReposDao {
        return appDatabase.getReposDao()
    }
}