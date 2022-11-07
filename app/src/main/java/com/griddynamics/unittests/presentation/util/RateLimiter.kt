package com.griddynamics.unittests.presentation.util

import android.os.SystemClock

import java.util.concurrent.TimeUnit

class CacheTimeLimiter<KEY>(timeout: Int, timeUnit: TimeUnit) {
    private val timestamps = HashMap<KEY, Long>()
    private val timeout = timeUnit.toMillis(timeout.toLong())

    @Synchronized
    fun shouldFetch(key: KEY): Boolean {
        val lastFetched = timestamps[key]
        val now = SystemClock.uptimeMillis()
        if (lastFetched == null) {
            timestamps[key] = now
            return true
        }
        if (now - lastFetched > timeout) {
            timestamps[key] = now
            return true
        }
        return false
    }

    @Synchronized
    fun reset(key: KEY) {
        timestamps.remove(key)
    }
}
