package com.example.personalspending.ui.screen.transaction

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.personalspending.SpendingApplication
import com.example.personalspending.data.ImageTransaction
import com.example.personalspending.repository.ImageRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.awaitResponse
import java.io.ByteArrayOutputStream
import java.io.File

class ImagesViewModel(private val imagesRepository: ImageRepository) : ViewModel() {

    fun uploadImage(id: Int, imageBitmap: ImageBitmap) {
        val file = File.createTempFile("upload", ".jpg")
        bitmapToFile(imageBitmap, file)

        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val idRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), id.toString())

        viewModelScope.launch {
            try {
                val response = imagesRepository.uploadImage(idRequestBody, body).awaitResponse()
                if (response.isSuccessful) {
                    println("Image uploaded successfully")
                } else {
                    println("Failed to upload image: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Error uploading image: ${e.message}")
            }
        }
    }

    fun getImages() {
        viewModelScope.launch {
            try {
                val response = imagesRepository.getImages()
                if (response.isSuccessful) {
                    val images = response.body()
                    if (images != null) {
                        // Process the list of images here
                        for (image in images) {
                            println("Image id: ${image.id}, url: ${image.url}")
                        }
                    } else {
                        println("Empty response body")
                    }
                } else {
                    println("Failed to get images: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Error getting images: ${e.message}")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SpendingApplication)
                val imagesViewModel = application.containerNetwork.imagesRepository
                ImagesViewModel(imagesViewModel)
            }
        }
    }
}


fun bitmapToFile(bitmap: ImageBitmap, file: File): File {
    val androidBitmap = bitmap.asAndroidBitmap()
    val outputStream = ByteArrayOutputStream()
    androidBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    file.writeBytes(byteArray)
    return file
}


