package com.gmail.movie_grid.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Result constructor(
    var adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("genre_ids")
    val genreIds: List<Int>,
    val id: Int,
    @SerializedName("original_language")
    val originalLanguage: String,
    @SerializedName("original_title")
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("release_date")
    val releaseDate: String,
    val title: String,
    val video: Boolean,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readString().toString(),
        TODO("genreIds"),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte(),
        parcel.readDouble(),
        parcel.readInt()
    ) {
    }

    constructor() : this(false, "", emptyList(), 0, "", "", "", 0.0, "", "", "", false, 0.0, 0)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (adult) 1 else 0)
        parcel.writeString(backdropPath)
        parcel.writeInt(id)
        parcel.writeString(originalLanguage)
        parcel.writeString(originalTitle)
        parcel.writeString(overview)
        parcel.writeDouble(popularity)
        parcel.writeString(posterPath)
        parcel.writeString(releaseDate)
        parcel.writeString(title)
        parcel.writeByte(if (video) 1 else 0)
        parcel.writeDouble(voteAverage)
        parcel.writeInt(voteCount)
       // parcel.writeList(genreIds)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Result> {
        override fun createFromParcel(parcel: Parcel): Result {
            return Result(parcel)
        }

        override fun newArray(size: Int): Array<Result?> {
            return arrayOfNulls(size)
        }
    }
}