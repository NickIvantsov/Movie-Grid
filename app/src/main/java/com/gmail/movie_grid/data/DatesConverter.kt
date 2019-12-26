package com.gmail.movie_grid.data

import androidx.room.TypeConverter
import com.gmail.movie_grid.model.Dates
import com.google.gson.Gson


class DatesConverter {

    @TypeConverter
    fun storeRepoOwnerToString(data: Dates): String = Gson().toJson(data)

    @TypeConverter
    fun storeStringToRepoOwner(value: String): Dates = Gson().fromJson(value,Dates::class.java)
}