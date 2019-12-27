package com.gmail.movie_grid.data.viewModel

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.gmail.movie_grid.model.Result

class SavedStateViewModel(state: SavedStateHandle) : ViewModel() {
    // Keep the key as a constant
    companion object {
        private const val LIST_STATE = "ListState"
        private const val FILMS_LIST = "FILMS_LIST"
    }

    private val savedStateHandle = state


    fun saveFilms(films: MutableList<Result>) {
        // Sets a new value for the object associated to the key.
        savedStateHandle.set(FILMS_LIST, films)
    }

    fun saveListState(listState: Parcelable?) {
        savedStateHandle.set(LIST_STATE, listState)
    }

    fun getListState(): Parcelable? {
        // Gets the current value of the user id from the saved state handle
        return savedStateHandle.get(LIST_STATE)
    }
    fun getFilms():MutableList<Result>? {
        return savedStateHandle.get(FILMS_LIST)
    }
}