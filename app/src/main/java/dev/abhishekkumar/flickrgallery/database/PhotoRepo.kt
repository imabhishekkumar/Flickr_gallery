package dev.abhishekkumar.flickrgallery.database

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import dev.abhishekkumar.flickrgallery.model.Photo

class PhotoRepo (application: Application){
private val photoDao:PhotoDao
    private val listLiveData: LiveData<List<Photo>>
    init {
        val photosRoomDatabase = PhotoDatabase.getInstance(application)
        photoDao=photosRoomDatabase?.photoDao()!!
        listLiveData= photoDao?.allPhotos()
    }
    fun getAllPhotos(): LiveData<List<Photo>>{
        return listLiveData
    }
    fun insert(note: Photo) {
         photoDao.insertPhotos(note)
    }
    fun update(note:Photo){
        return photoDao.updatePhotos(note)
    }

    private class insertAsyncTask internal  constructor(private val mAsyncTaskDao: PhotoDao): AsyncTask<Photo, Void, Void>(){
        override fun doInBackground(vararg p0: Photo): Void? {
            mAsyncTaskDao.insertPhotos(p0[0])
            return null
        }

    }
}