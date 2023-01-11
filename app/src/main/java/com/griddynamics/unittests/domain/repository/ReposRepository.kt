package com.griddynamics.unittests.domain.repository

import androidx.lifecycle.LiveData
import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.domain.model.Repo

interface ReposRepository {

    fun getReposByUser(user: String): LiveData<Result<List<Repo>>>
}