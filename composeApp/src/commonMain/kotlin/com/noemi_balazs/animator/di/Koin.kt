package com.noemi_balazs.animator.di

import com.noemi_balazs.animator.MainViewModel
import com.noemi_balazs.animator.data.database.AnimatorDataBase
import com.noemi_balazs.animator.data.datastore.AppDataStore
import com.noemi_balazs.animator.data.repository.AnimatorRepository
import com.noemi_balazs.animator.data.repository.AnimatorRepositoryImpl
import com.noemi_balazs.animator.feature.animator.AnimatorViewModel
import com.noemi_balazs.animator.feature.details.DetailsViewModel
import com.noemi_balazs.animator.feature.favorite.FavoriteViewModel
import com.noemi_balazs.animator.feature.selector.SelectorViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

expect class AnimatorImage
expect fun AnimatorImage.getBytes(): ByteArray?
expect val platformModule: Module
val domainModule = module {
    single { AppDataStore(get()) }

    single<AnimatorRepository> {
        val database: AnimatorDataBase = get()
        AnimatorRepositoryImpl(database.getAnimatorDao())
    }
}
val viewModelModule = module {
    factoryOf(::MainViewModel)
    factoryOf(::SelectorViewModel)
    factoryOf(::AnimatorViewModel)
    factoryOf(::FavoriteViewModel)
    factoryOf(::DetailsViewModel)
}
fun getAppModules(): List<Module> = listOf(
    domainModule,
    platformModule,
    viewModelModule
)

fun initKoin() {

    startKoin {
        modules(getAppModules())
    }
}