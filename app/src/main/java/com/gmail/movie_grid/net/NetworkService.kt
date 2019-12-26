package com.gmail.movie_grid.net

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object NetworkService {
    // const val API_KEY = "1f71bb7ac9eb935e91dad65b12b18354"
    private var mRetrofit: Retrofit? = null
    private val BASE_URL = "http://api.themoviedb.org"
    const val POSTER_URL = "http://image.tmdb.org/t/p/w342/"

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
    }

    fun getJSONApi(): Api {
        return mRetrofit!!.create<Api>(Api::class.java)
    }
}