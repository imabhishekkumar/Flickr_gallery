package dev.abhishekkumar.flickrgallery.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "photos")
class Photo {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("url_s")
    @Expose
    var urlS: String? = null

}
