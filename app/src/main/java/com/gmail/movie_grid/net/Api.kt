package com.gmail.movie_grid.net

import com.gmail.movie_grid.model.Response
import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.Query


interface Api {
    @POST("/3/movie/now_playing?api_key=1f71bb7ac9eb935e91dad65b12b18354")
    fun getPostWithID(): Observable<Response?>?

    @POST("/3/movie/now_playing?api_key=1f71bb7ac9eb935e91dad65b12b18354")
    fun getPostWithID(@Query("page") page: Int): Observable<Response?>?

    @POST("/3/movie/now_playing?api_key=1f71bb7ac9eb935e91dad65b12b18354")
    fun getPostWithID(@Query("page") page: Int, @Query("api_key") apiKey: String): Observable<Response?>?
}