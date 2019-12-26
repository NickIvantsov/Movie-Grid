package com.gmail.movie_grid.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gmail.movie_grid.data.DatesConverter
import com.gmail.movie_grid.data.ResultsCoverter
import com.google.gson.annotations.SerializedName


@Entity(tableName = "films")
@TypeConverters(ResultsCoverter::class, DatesConverter::class)
data class Response constructor(
    val id: Int,
    val dates: Dates? = null,
    @PrimaryKey
    val page: Int,
    val results: List<Result>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
) {
    constructor() : this(0, null, 0, emptyList(), 0, 0)
}