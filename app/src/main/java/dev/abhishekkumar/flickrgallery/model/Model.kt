package dev.abhishekkumar.flickrgallery.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Model {
    @SerializedName("photos")
    @Expose
    var photos: Photos? = null
}

