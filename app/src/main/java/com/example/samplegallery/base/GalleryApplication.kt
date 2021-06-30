package com.example.samplegallery.base

import android.app.Application
import com.example.samplegallery.dagger.ApplicationComponent
import com.example.samplegallery.dagger.DaggerApplicationComponent

class GalleryApplication : Application() {
    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: GalleryApplication
    }
}