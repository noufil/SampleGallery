package com.example.samplegallery.home.network

import com.example.samplegallery.base.Config
import com.example.samplegallery.base.IMAGE_SEARCH
import com.example.samplegallery.home.models.ImageSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageService {
    companion object {
        const val QUERY = "query"
        const val CLIENT_ID = "client_id"
        const val ORIENTATION = "orientation"
        const val PAGE = "page"
        const val PER_PAGE = "per_page"
    }

    @GET(IMAGE_SEARCH)
    suspend fun searchPhotos(
        @Query(QUERY) query: String,
        @Query(PAGE) page: Int,
        @Query(CLIENT_ID) clientId: String = Config.CLIENT_ID,
        @Query(ORIENTATION) orientation: String = Config.ORIENTATION,
        @Query(PER_PAGE) pageSize: Int = Config.PAGE_SIZE
    ): ImageSearchResponse
}