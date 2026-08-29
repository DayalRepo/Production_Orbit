package com.orbitai.erp.di

import com.orbitai.erp.core.data.di.dataModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

/** ViewModel bindings. Populated as feature modules are added. */
val viewModelModule = module {
}

val appModules = listOf(dataModule, viewModelModule)

/**
 * Starts Koin. Called from `OrbitApplication` on Android and `iOSApp.init` on iOS.
 *
 * Kept parameterless so the generated Objective-C signature stays `doInitKoin()`.
 */
fun initKoin() {
    startKoin {
        modules(appModules)
    }
}
