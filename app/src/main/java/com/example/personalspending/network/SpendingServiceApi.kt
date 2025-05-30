package com.example.personalspending.network

import android.net.http.HttpResponseCache.install
import android.util.Base64
import android.util.Log
import com.example.personalspending.data.ImageTransaction
import com.example.personalspending.repository.AppContainerNetwork
import com.example.personalspending.repository.ImageRepository
import com.example.personalspending.repository.NetworkImagesRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File
import java.net.SocketTimeoutException

class DefaultAppContainer: AppContainerNetwork {

    val BASE_URL = "http://localhost:3000/"

    val json = Json { ignoreUnknownKeys = true }
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()


    private val retrofitService: ImageApiService by lazy {
        retrofit.create(ImageApiService::class.java)
    }

    override val imagesRepository: ImageRepository by lazy {
        NetworkImagesRepository(retrofitService)
    }
}

interface ImageApiService {
    @GET("images")
    fun getAllBooks(): Response<List<ImageTransaction>>

    @Multipart
    @POST("books")
    fun createBook(@Part file: MultipartBody.Part, @Part("id") id: RequestBody): Call<ImageTransaction>

    @Multipart
    @PUT("books/{id}")
    fun updateBook(@Path("id") id: Int, @Body image: ImageTransaction): Response<ImageTransaction>

    @DELETE("books/{id}")
    fun deleteBook(@Path("id") id: Int): Response<Void>
}

object ApiClient {
    private const val BASE_URL = "http://192.168.1.8:3000/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

interface ApiService {
    @GET("images")
    fun getUsers(): Call<List<ImageTransaction>>

    @POST("images")
    fun createImage(@Body image: ImageTransaction): Call<ImageTransaction>

    @PUT("images/{id}")
    fun updateImage(@Path("id") id: String, @Body image: ImageTransaction): Call<ImageTransaction>

    @DELETE("images/{id}")
    fun deleteImage(@Path("id") id: String): Call<Void>
}


class ImageController {
    private val apiService: ApiService = ApiClient.instance.create(ApiService::class.java)

    fun fetchImages(onResult: (List<ImageTransaction>?) -> Unit) {
        val call = apiService.getUsers()
        call.enqueue(object : Callback<List<ImageTransaction>> {
            override fun onResponse(call: Call<List<ImageTransaction>>, response: Response<List<ImageTransaction>>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<List<ImageTransaction>>, t: Throwable) {
                Log.e("UserController", "Error fetching users", t)
                onResult(null)
            }
        })
    }

    fun createImage(image: ImageTransaction, onResult: (ImageTransaction?) -> Unit) {
        val call = apiService.createImage(image)
        call.enqueue(object : Callback<ImageTransaction> {
            override fun onResponse(call: Call<ImageTransaction>, response: Response<ImageTransaction>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    Log.e("ImageController", "Response error: ${response.code()}")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<ImageTransaction>, t: Throwable) {
                Log.e("ImageController", "Error creating image", t)
                onResult(null)
            }
        })
    }

    fun updateImage(image: ImageTransaction, onResult: (ImageTransaction?) -> Unit) {
        val call = apiService.updateImage(image.id, image)
        call.enqueue(object : Callback<ImageTransaction> {
            override fun onResponse(call: Call<ImageTransaction>, response: Response<ImageTransaction>) {
                if (response.isSuccessful) {
                    onResult(response.body())
                } else {
                    Log.e("ImageController", "Response error: ${response.code()}")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<ImageTransaction>, t: Throwable) {
                Log.e("ImageController", "Error updating image", t)
                onResult(null)
            }
        })
    }

    fun deleteImage(id: String, onResult: (Boolean) -> Unit) {
        val call = apiService.deleteImage(id)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onResult(true)
                } else {
                    Log.e("ImageController", "Response error: ${response.code()}")
                    onResult(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ImageController", "Error deleting image", t)
                onResult(false)
            }
        })
    }
}
