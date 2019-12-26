package com.gmail.movie_grid.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.gmail.movie_grid.data.db.FilmsDatabase
import com.gmail.movie_grid.data.repository.FilmRepository
import com.gmail.movie_grid.model.Response
import kotlinx.coroutines.launch

class FilmsViewModel(application: Application) : AndroidViewModel(application) {
    // The ViewModel maintains a reference to the repository to get data.
    private val repository: FilmRepository
    // LiveData gives us updated words when they change.
    val allResponses: LiveData<List<Response>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val filmsDao = FilmsDatabase.getDatabase(application).filmDao()
        repository = FilmRepository(filmsDao)
        allResponses = repository.allFilms
    }

    fun insert(word: Response?) = viewModelScope.launch {
        repository.insert(word)
    }
}