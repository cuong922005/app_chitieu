package com.example.personalspending.repository

import com.example.personalspending.data.ImageTransaction
import com.example.personalspending.network.ImageApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

interface ImageRepository {
    suspend fun getImages(): Response<List<ImageTransaction>>
    suspend fun uploadImage(id: RequestBody, file: MultipartBody.Part): Call<ImageTransaction>
}

class NetworkImagesRepository(
    private val imagesApiService: ImageApiService
): ImageRepository {
    override suspend fun getImages(): Response<List<ImageTransaction>> {
        return imagesApiService.getAllBooks()
    }

    override suspend fun uploadImage(id: RequestBody, file: MultipartBody.Part): Call<ImageTransaction> {
        return imagesApiService.createBook(file, id)
    }
}