package com.example.exams.api

import com.example.exams.Models.QuizModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface QuizApi {
    @GET("api/quizzes")
    suspend fun getQuizzes(): Response<List<QuizModel>>

    @POST("api/quizzes")
    suspend fun createQuiz(@Body quiz: QuizModel): Response<Unit>

    @DELETE("api/quizzes/{id}")
    suspend fun deleteQuiz(@Path("id") id: String): Response<Unit>

    @PUT("api/quizzes/{id}")
    suspend fun updateQuiz(@Path("id") id: String, @Body quiz: QuizModel): Response<Unit>
}

object RetrofitClient {
    //    private const val BASE_URL = "http://quizzes0.runasp.net/"
    private const val BASE_URL = "http://10.0.2.2:5000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val quizApi: QuizApi by lazy {
        retrofit.create(QuizApi::class.java)
    }
}