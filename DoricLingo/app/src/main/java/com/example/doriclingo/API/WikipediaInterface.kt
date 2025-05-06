package com.example.doriclingo.API

import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaInterface {
    //HTTP GET request to fetch a summary of a Wikipedia page
    @GET("page/summary/{title}")
    suspend fun getSummary (@Path("title") title: String): Wikipedia
}