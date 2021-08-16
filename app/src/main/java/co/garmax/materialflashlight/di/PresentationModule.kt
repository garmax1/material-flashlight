package co.garmax.materialflashlight.di

import co.garmax.materialflashlight.ui.main.MainViewModel
import co.garmax.materialflashlight.ui.root.RootViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { RootViewModel(get(), get(), get()) }
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
}