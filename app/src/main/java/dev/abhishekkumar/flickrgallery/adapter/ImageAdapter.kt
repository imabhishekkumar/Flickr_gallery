package dev.abhishekkumar.flickrgallery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide

import java.util.ArrayList
import dev.abhishekkumar.flickrgallery.R
import dev.abhishekkumar.flickrgallery.model.Photo

class ImageAdapter(context: Context) : RecyclerView.Adapter<ImageAdapter.Holder>() {
    private var context: Context? = null
    private val modelList: MutableList<Photo>?
    private val layoutInflater: LayoutInflater
    private var isLoadingAdded = false

    val isEmpty: Boolean
        get() = itemCount == 0

    init {
        layoutInflater = LayoutInflater.from(context)
        modelList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photo_row,
                        parent,
                        false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val photo = modelList!![position]
        Glide.with(holder.imageView!!.context)
                .load(photo.urlS)
                .into(holder.imageView!!)
        holder.textView!!.text = photo.title
    }

    override fun getItemCount(): Int {
        return modelList?.size ?: 0
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        val textView = itemView.findViewById<TextView>(R.id.textView)


    }

    fun add(photo: Photo) {
        modelList!!.add(photo)
        notifyItemInserted(modelList.size - 1)
    }

    fun addAll(mcList: List<Photo>) {
        for (mc in mcList) {
            add(mc)
        }
    }

    fun remove(city: Photo?) {
        val position = modelList!!.indexOf(city)
        if (position > -1) {
            modelList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Photo())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = modelList!!.size - 1
        val item = getItem(position)
        if (item != null) {
            modelList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): Photo? {
        return modelList!![position]
    }
}
