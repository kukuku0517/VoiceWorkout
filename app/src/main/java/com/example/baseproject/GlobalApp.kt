package com.example.baseproject

import android.app.Application
import com.example.baseproject.di.data
import com.example.baseproject.di.domain
import com.example.baseproject.di.presentation
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class GlobalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoinModules(false)
    }

    fun startKoinModules(refresh: Boolean) {
        if (refresh) {
            stopKoin()

        }
        startKoin {
            androidContext(this@GlobalApp)
            modules(
                listOf(
                    data,
                    presentation,
                    domain
                )
            )
        }
    }
}
