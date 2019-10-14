package dev.abhishekkumar.flickrgallery.api

import dev.abhishekkumar.flickrgallery.model.Model
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitClient {
    @GET("/services/rest/?method=flickr.photos.getRecent&per_page=20&api_key=6f102c62f41998d151e5a1b48713cf13&format=json&nojsoncallback=1&extras=url_s")
    fun getPhotosByPage(@Query("page") tags: String): Call<Model>

    @GET("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=6f102c62f41998d151e5a1b48713cf13&extras=url_s&per_page=20&format=json&nojsoncallback=1")
    fun getPhotosByTag(@Query("tags") tags: String): Call<Model>
}
