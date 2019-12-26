package com.gmail.movie_grid.data.repository

import androidx.lifecycle.LiveData
import com.gmail.movie_grid.data.dao.FilmDao
import com.gmail.movie_grid.model.Response

class FilmRepository(private val filmDao: FilmDao) {
    val allFilms: LiveData<List<Response>> = filmDao.getAllFilms()

    suspend fun insert(response: Response?) {
        filmDao.insert(response)
    }
}