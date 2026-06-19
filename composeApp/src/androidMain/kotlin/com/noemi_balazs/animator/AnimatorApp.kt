package com.noemi_balazs.animator

import android.app.Application
import com.noemi_balazs.animator.di.getAppModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AnimatorApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@AnimatorApp)
            modules(getAppModules())
        }
    }
}