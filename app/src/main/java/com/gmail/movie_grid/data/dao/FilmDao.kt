package com.gmail.movie_grid.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gmail.movie_grid.data.DatesConverter
import com.gmail.movie_grid.data.ResultsCoverter
import com.gmail.movie_grid.model.Response

@Dao
@TypeConverters(DatesConverter::class, ResultsCoverter::class)
interface FilmDao {
    @Query("SELECT * from films")
    fun getAllFilms(): LiveData<List<Response>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(response: Response?)

    @Query("DELETE FROM films")
    suspend  fun deleteAll()
}