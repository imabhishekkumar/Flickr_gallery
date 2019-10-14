package dev.abhishekkumar.flickrgallery.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.abhishekkumar.flickrgallery.model.Photo
import kotlinx.coroutines.CoroutineScope
@Database(entities = arrayOf(Photo::class),version = 1)
abstract class PhotoDatabase :RoomDatabase(){
    abstract fun photoDao(): PhotoDao


    companion object {

        private val LOCK = PhotoDatabase::class
        private val DATABASE_NAME = "noteDatabase"
        private var sInstance: PhotoDatabase? = null

        fun getInstance(context: Context): PhotoDatabase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            PhotoDatabase::class.java, DATABASE_NAME
                    )
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return sInstance
        }
        private class WordDatabaseCallback(
                private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */

        }
        fun destroyInstance() {
            sInstance = null
        }
    }
}
