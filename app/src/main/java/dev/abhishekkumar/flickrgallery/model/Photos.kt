package dev.abhishekkumar.flickrgallery.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Photos {

    @SerializedName("photo")
    @Expose
    var photo: List<Photo>? = null
}
