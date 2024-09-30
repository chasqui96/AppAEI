package com.example.appaei.data.network
import com.example.appaei.data.model.LoginpageModel
import com.example.appaei.data.model.LoginPageResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginpageModel): Response<LoginPageResponse>
}