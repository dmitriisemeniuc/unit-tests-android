package com.dmitriisemeniuc.unittests.domain.repository

import androidx.lifecycle.LiveData
import com.dmitriisemeniuc.unittests.common.net.Result
import com.dmitriisemeniuc.unittests.domain.model.Repo

interface ReposRepository {

    fun getReposByUser(user: String): LiveData<Result<List<Repo>>>
}