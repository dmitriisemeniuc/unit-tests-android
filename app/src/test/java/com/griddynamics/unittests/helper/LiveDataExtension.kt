package com.griddynamics.unittests.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.mockk.*

class LiveDataValueCapture<T> {

    val lock = Any()

    private val _values = mutableListOf<T?>()
    val values: List<T?>
        get() = synchronized(lock) {
            _values.toList() // copy to avoid returning reference to mutable list
        }

    fun addValue(value: T?) = synchronized(lock) {
        _values += value
    }
}

inline fun <T> LiveData<T>.captureValues(block: LiveDataValueCapture<T>.() -> Unit) {
    val capture = LiveDataValueCapture<T>()
    val observer = Observer<T> {
        capture.addValue(it)
    }
    observeForever(observer)
    try {
        capture.block()
    } finally {
        removeObserver(observer)
    }
}

inline fun <reified T : Any> LiveData<T>.captureValues(): List<T> {
    val observer = mockk<Observer<T>>()
    val slot = slot<T>()
    val list = mutableListOf<T>()
    every { observer.onChanged(capture(slot)) } answers {
        list.add(slot.captured)
    }
    this.observeForever(observer)
    return list
}