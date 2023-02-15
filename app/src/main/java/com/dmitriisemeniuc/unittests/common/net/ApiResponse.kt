package com.dmitriisemeniuc.unittests.common.net

sealed class ApiResponse<T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error<T>(val reason: Throwable) : ApiResponse<T>()
}