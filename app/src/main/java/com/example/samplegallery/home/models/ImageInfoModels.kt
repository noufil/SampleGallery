package com.example.samplegallery.home.models

import com.google.gson.annotations.SerializedName

class ImageSearchResponse {
    @SerializedName("total")
    var total: Int = 0

    @SerializedName("total_pages")
    var totalPages: Int = 0

    @SerializedName("results")
    var results: MutableList<ImageInfo>? = null

    fun merge(imageSearchResponse: ImageSearchResponse?) {
        imageSearchResponse?.let {
            total = it.total
            totalPages = it.totalPages
            val elements = imageSearchResponse.results
            if (results.isNullOrEmpty()) {
                results = elements
                return
            }
            if (!elements.isNullOrEmpty()) {
                results!!.addAll(elements)
            }
        }
    }
}


class ImageInfo {
    @SerializedName("color")
    var color: String? = null

    @SerializedName("urls")
    var urls: ImageUrls? = null
}

class ImageUrls {
    @SerializedName("small")
    var smallSize: String? = null
}