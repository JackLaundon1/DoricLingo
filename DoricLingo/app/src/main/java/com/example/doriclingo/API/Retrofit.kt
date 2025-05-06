package com.example.doriclingo.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    //creates instance of wikipedia API using retrofit
    val api: WikipediaInterface = Retrofit.Builder()
        //sets base URL
        .baseUrl("https://en.wikipedia.org/api/rest_v1/")
        .addConverterFactory(GsonConverterFactory.create())
        //builds retrofit instance
        .build()
        .create(WikipediaInterface::class.java)
}
