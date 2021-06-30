package com.example.samplegallery.dagger

import com.example.samplegallery.base.Config
import com.example.samplegallery.base.DOMAIN
import com.example.samplegallery.base.GalleryApplication
import com.example.samplegallery.home.network.ImageService
import com.example.samplegallery.utils.hasNetwork
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ApplicationModule {

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @Provides
    fun provideImageService(retrofit: Retrofit): ImageService =
        retrofit.create(ImageService::class.java)

    @Provides
    fun provideAppContext() = GalleryApplication.instance

    @Provides
    fun provideOkHttpClient(cache: Cache, galleryApplication: GalleryApplication) =
        OkHttpClient.Builder()
            .cache(cache)
            .addNetworkInterceptor(Interceptor { chain ->
                var request = chain.request()
                request = if (galleryApplication.hasNetwork())
                    request.newBuilder()
                        .header("Cache-Control", "public, max-age=5")
                        .build()
                else
                    request.newBuilder().header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=${Config.CACHE_MAX_AGE}"
                    ).build()
                //Replace server header to allow caching
                val response: Response = chain.proceed(request)
                response.newBuilder()
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=${Config.CACHE_MAX_AGE}").build()
            }).build()

    @Provides
    fun provideNetworkCache(galleryApplication: GalleryApplication): Cache =
        Cache(galleryApplication.cacheDir, Config.CACHE_SIZE)

}