package com.gmail.movie_grid.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gmail.movie_grid.data.DatesConverter
import com.gmail.movie_grid.data.ResultsCoverter
import com.gmail.movie_grid.data.dao.FilmDao
import com.gmail.movie_grid.model.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Response::class], version = 4, exportSchema = false)
@TypeConverters(DatesConverter::class,ResultsCoverter::class)
abstract class FilmsDatabase : RoomDatabase() {

    abstract fun filmDao(): FilmDao

   companion object {
       @Volatile
       private var INSTANCE: FilmsDatabase? = null

       fun getDatabase(
           context: Context
       ): FilmsDatabase {
           // if the INSTANCE is not null, then return it,
           // if it is, then create the database
           return INSTANCE ?: synchronized(this) {
               val instance = Room.databaseBuilder(
                   context.applicationContext,
                   FilmsDatabase::class.java,
                   "word_database")
                   .fallbackToDestructiveMigration()
                   .build()
               INSTANCE = instance
               // return instance
               instance
           }
       }
   }
    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        /**
         * Override the onOpen method to populate the database.
         * For this sample, we clear the database every time it is created or opened.
         */
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // If you want to keep the data through app restarts,
            // comment out the following line.
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.filmDao())
                }
            }
        }

        suspend fun populateDatabase(wordDao: FilmDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            wordDao.deleteAll()

        }
    }


}
