package com.example.samplegallery.dagger

import com.example.samplegallery.home.viewModels.HomeViewModel
import dagger.Component

@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(viewModel: HomeViewModel)
}