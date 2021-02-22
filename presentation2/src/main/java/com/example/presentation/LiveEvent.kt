package com.example.presentation

import androidx.lifecycle.*
import androidx.lifecycle.Observer


open class LiveEvent<T> {

    enum class EventType {
        PUBLISH,
        BEHAVIOR
    }

    val liveData = MutableLiveData<Event<T>>()

    private val observerMap = mutableMapOf<Observer<T>, Observer<Event<T>>>()


    fun get(): LiveData<Event<T>> {
        return liveData
    }

    open fun replaceObserver(
            owner: LifecycleOwner,
            observer: Observer<T>,
            type: EventType = EventType.PUBLISH
    ) {
        removeAllObservers(owner)
        observe(owner, observer, type)
    }

    open fun observe(
            owner: LifecycleOwner,
            observer: Observer<T>,
            type: EventType = EventType.PUBLISH
    ) {

        when (type) {
            EventType.PUBLISH -> {
                val innerObserver = Observer<Event<T>> { event ->
                    event.getContentIfNotHandled()?.let {
                        observer.onChanged(it)
                    }
                }
                liveData.observe(owner, innerObserver)
                observerMap[observer] = innerObserver
            }
            EventType.BEHAVIOR -> {
                val innerObserver = Observer<Event<T>> { event ->
                    observer.onChanged(event.peekContent())
                }
                liveData.observe(owner, innerObserver)
                observerMap[observer] = innerObserver
            }
        }
    }

    fun removeObserver(observer: Observer<T>) {
        val innerObserver = observerMap.remove(observer)
        innerObserver?.let {
            liveData.removeObserver(it)
        }
    }

    fun removeAllObservers(lifecycleOwner: LifecycleOwner) {
        for ((k, v) in observerMap) {
            liveData.removeObserver(v)
            observerMap.remove(k)
        }
        observerMap.clear()
        liveData.removeObservers(lifecycleOwner)
    }


}

class MutableLiveEvent<T>() : LiveEvent<T>() {

    constructor(value: T) : this() {
        this.value = value
    }

    var value: T? = null
        set(value) {
            if (value != null) {
                field = value
                liveData.value = Event(value)
            }
        }

    fun postValue(value: T) {
        liveData.postValue(Event(value))
    }
}

class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

fun <T> MutableLiveData<T>.set(copy: () -> T) {
    this.value = (copy())
}


fun <T> MutableLiveData<T>.update(copy: (T) -> T) {
    this.value = (copy(this.value!!))
}
