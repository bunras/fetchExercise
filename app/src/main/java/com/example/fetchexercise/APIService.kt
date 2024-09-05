package com.example.fetchexercise

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("hiring.json")
    fun getItems(): Call<List<Item>>
}
