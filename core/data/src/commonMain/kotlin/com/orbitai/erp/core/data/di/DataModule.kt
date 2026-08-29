package com.orbitai.erp.core.data.di

import com.orbitai.erp.core.data.session.FakeSessionRepository
import com.orbitai.erp.core.data.session.SessionRepository
import org.koin.dsl.module

/**
 * Data-layer bindings. Currently backed by in-memory fakes so the UI can be built and previewed
 * before the Supabase and Kotlin backend integrations land.
 */
val dataModule = module {
    single { FakeSessionRepository() }
    single<SessionRepository> { get<FakeSessionRepository>() }
}
