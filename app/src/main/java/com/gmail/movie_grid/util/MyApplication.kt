package com.gmail.movie_grid.util

import android.app.Application
import com.facebook.stetho.Stetho


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Create an InitializerBuilder
        // Create an InitializerBuilder
        val initializerBuilder = Stetho.newInitializerBuilder(this)

        // Enable Chrome DevTools
        // Enable Chrome DevTools
        initializerBuilder.enableWebKitInspector(
            Stetho.defaultInspectorModulesProvider(this)
        )

        // Enable command line interface
        // Enable command line interface
        initializerBuilder.enableDumpapp(
            Stetho.defaultDumperPluginsProvider(this)
        )
        // Use the InitializerBuilder to generate an Initializer
        // Use the InitializerBuilder to generate an Initializer
        val initializer: Stetho.Initializer = initializerBuilder.build()

        // Initialize Stetho with the Initializer
        // Initialize Stetho with the Initializer
        Stetho.initialize(initializer)
    }
}