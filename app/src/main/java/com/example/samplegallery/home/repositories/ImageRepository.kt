package com.example.samplegallery.home.repositories

import com.example.samplegallery.home.models.ImageSearchResponse
import com.example.samplegallery.home.network.ImageService
import javax.inject.Inject

class ImageRepository @Inject constructor(private val imageService: ImageService) {
    suspend fun searchImages(userQuery: String, page: Int): ImageSearchResponse {
        return imageService.searchPhotos(query = userQuery, page = page)
    }
}