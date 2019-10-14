package dev.abhishekkumar.flickrgallery.database

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.abhishekkumar.flickrgallery.model.Photo

@Dao
interface PhotoDao{

    @Query("SELECT * FROM photos")
    fun allPhotos():LiveData<List<Photo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPhotos(photoResult: Photo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePhotos(photoResult: Photo)

    @Delete
    fun deletePhotos(photoResult: Photo)

}