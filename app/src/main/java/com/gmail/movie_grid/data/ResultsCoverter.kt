package com.gmail.movie_grid.data

import androidx.room.TypeConverter
import com.gmail.movie_grid.model.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ResultsCoverter {
    @TypeConverter
    fun storeRepoOwnerToString(data: List<Result>): String = Gson().toJson(data)

    @TypeConverter
    fun storeStringToRepoOwner(value: String): List<Result> {

        val listType: Type = object : TypeToken<List<Result>>() {}.type
        return Gson().fromJson(value, listType)
    }

}