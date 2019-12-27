package com.gmail.movie_grid.util

import android.util.Log
import com.gmail.movie_grid.model.Result

class FilmFilter(private val id: Int) {

    fun filter(list: List<Result>): List<Result> {
        val tmp = ArrayList<Result>()
        list.forEach(action = {
            if (id == it?.id) {
                tmp.add(it)
                Log.d(TAG,"add new element")
            }
        })
        return tmp
    }

    companion object{
        const val TAG = "FilmFilter"
    }
}