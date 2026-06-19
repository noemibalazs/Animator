package com.noemi_balazs.animator.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

open class BaseViewModel : ViewModel() {

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    suspend fun <T> withProgress(block: suspend () -> T): T {
        _isLoading.value = true
        return try {
            block()
        } finally {
            _isLoading.value = false
        }
    }

    fun runCoroutine(block: suspend CoroutineScope.() -> Unit): Job =
        viewModelScope.launch(
            context = EmptyCoroutineContext,
            start = CoroutineStart.DEFAULT,
            block = block
        )

    suspend fun <T> Flow<T>.collectFlow(collector: FlowCollector<T>) {
        collect(collector)
    }
}