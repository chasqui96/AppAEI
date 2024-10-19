package com.example.appaei.data.network
import com.example.appaei.data.model.LoginpageModel
import com.example.appaei.data.model.LoginPageResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("/upload")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>
}