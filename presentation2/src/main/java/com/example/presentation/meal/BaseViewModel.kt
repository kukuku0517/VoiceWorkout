package com.example.presentation.meal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.presentation.MutableLiveEvent

abstract class BaseViewModel<STATE> : ViewModel() {
    val state = stateOf<STATE>()
}


fun <T> stateOf(default: T? = null): MutableLiveData<T> {
    return MutableLiveData<T>().apply {
        default?.let {
            value = it
        }
    }
}

fun <T> eventOf(default: T? = null): MutableLiveEvent<T> {
    return MutableLiveEvent<T>().apply {
        default?.let {
            value = it
        }
    }
}