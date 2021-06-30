package com.example.samplegallery.home.viewModels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.samplegallery.R
import com.example.samplegallery.base.GalleryApplication
import com.example.samplegallery.home.adapters.PageRequestListener
import com.example.samplegallery.home.models.ImageSearchResponse
import com.example.samplegallery.home.repositories.ImageRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel(val context: Application) : AndroidViewModel(context), PageRequestListener {
    init {
        (context as GalleryApplication).appComponent.inject(this)
    }

    @Inject
    lateinit var imageRepository: ImageRepository
    val searchResponse: MutableLiveData<ImageSearchResponse?> = MutableLiveData()
    val searchError: ObservableField<String> = ObservableField("")
    var previousQuery: String = ""
    var previousPage: Int = 0
    var isLoading: Boolean = false
    var columns = 2

    //Being used for position of shared element
    var currentPosition = 0

    fun searchData(userQuery: String, page: Int) {
        isLoading = true
        viewModelScope.launch {
            try {
                if (page == 1)
                    searchResponse.value = null
                val response = imageRepository.searchImages(userQuery, page)

                if (response.total == 0) {
                    previousQuery = ""
                    previousPage = 0
                    searchResponse.value = response
                    updateError(context.getString(R.string.no_result_found))
                    return@launch
                }

                previousQuery = userQuery
                previousPage = page

                val value = searchResponse.value
                if (value != null) {
                    value.merge(response)
                    searchResponse.value = value
                } else
                    searchResponse.value = response
            } catch (httpException: Throwable) {
                if (page == 1) {//Set blank response to stop showing loading
                    previousQuery = ""
                    previousPage = 0
                    searchResponse.value = ImageSearchResponse()
                }
                updateError(context.getString(R.string.something_went_wrong))
            } finally {
                isLoading = false
            }
        }
    }

    private fun updateError(message: String) {
        val oldMessage = searchError.get()
        if (oldMessage == message)
            searchError.notifyChange()
        else
            searchError.set(message)
    }

    override fun nextPage() {
        if (isLoading)
            return
        searchResponse.value?.let {
            if (previousPage < it.totalPages) {
                searchData(previousQuery, previousPage + 1)
            }
        }
    }
}