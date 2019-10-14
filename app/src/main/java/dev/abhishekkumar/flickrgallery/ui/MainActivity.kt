package dev.abhishekkumar.flickrgallery.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import android.app.Dialog
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import dev.abhishekkumar.flickrgallery.R
import dev.abhishekkumar.flickrgallery.adapter.ImageAdapter
import dev.abhishekkumar.flickrgallery.api.RetrofitClient
import dev.abhishekkumar.flickrgallery.database.PhotoRepo
import dev.abhishekkumar.flickrgallery.model.Model
import dev.abhishekkumar.flickrgallery.model.Photo
import dev.abhishekkumar.flickrgallery.utils.ConnectivityReceiver
import dev.abhishekkumar.flickrgallery.utils.MyApplication
import dev.abhishekkumar.flickrgallery.utils.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {


    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val retrofitClient: RetrofitClient = retrofit.create(RetrofitClient::class.java)
    private var imageAdapter: ImageAdapter? = null
    private var isLoading = false
    private var isLastPage = false
    private var currentPageNumber = PAGE_START
    private val TOTAL_PAGES = 50
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val repo = PhotoRepo(application)
        recyclerView!!.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView!!.layoutManager = gridLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()

        imageAdapter = ImageAdapter(this@MainActivity)
        recyclerView!!.adapter = imageAdapter



        recyclerView!!.addOnScrollListener(object : PaginationScrollListener(gridLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPageNumber += 1

                loadNextPage(retrofitClient, currentPageNumber)
            }

            override fun getTotalPageCount(): Int {
                return TOTAL_PAGES
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })

        bottomNavigationView!!.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_recent -> {
                    currentPageNumber = 1
                    getRecent(retrofitClient, currentPageNumber)
                    true
                }
                R.id.action_search -> {
                    performSearch(retrofitClient)
                    true
                }
                R.id.action_about -> {
                    showProfile()
                    true
                }
            }
            false
        }
        checkConnection()
    }



    private fun loadNextPage(retrofitClient: RetrofitClient, currentPageNumber: Int) {
        progressBar!!.visibility = View.VISIBLE
        val call = retrofitClient.getPhotosByPage(currentPageNumber.toString())
        call.enqueue(object : Callback<Model> {
            override fun onResponse(call: Call<Model>, response: Response<Model>) {
                if (response.isSuccessful) {
                    for (models in response.body()!!.photos!!.photo!!) {
                        Log.d("Response", models.urlS)
                    }
                    imageAdapter!!.addAll(response.body()!!.photos!!.photo!!)
                    imageAdapter!!.removeLoadingFooter()
                    isLoading = false
                    progressBar!!.visibility = View.GONE
                    if (currentPageNumber != TOTAL_PAGES)
                        imageAdapter!!.addLoadingFooter()
                    else
                        isLastPage = true
                }
            }

            override fun onFailure(call: Call<Model>, t: Throwable) {
                Log.d("Failure", t.message)
            }
        })
    }

    private fun performSearch(retrofitClient: RetrofitClient) {
        val dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.search_dialog)
        val searchET = dialog.findViewById<EditText>(R.id.searchET)
        val searchBtn = dialog.findViewById<TextView>(R.id.searchBtn)
        val cancelBtn = dialog.findViewById<TextView>(R.id.cancelBtn)
        searchBtn.setOnClickListener { view ->
            val keyword = searchET.text.toString()
            search(retrofitClient, keyword)
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun search(retrofitClient: RetrofitClient, keyword: String) {
        frameLayout!!.visibility = View.VISIBLE
        constraintLayout!!.visibility = View.INVISIBLE
        val call = retrofitClient.getPhotosByTag(keyword)

        call.enqueue(object : Callback<Model> {
            override fun onResponse(call: Call<Model>, response: Response<Model>) {
                if (response.isSuccessful) {
                    for (models in response.body()!!.photos!!.photo!!) {
                        Log.d("Response", models.urlS)
                    }
                    imageAdapter!!.clear()
                    imageAdapter!!.addAll(response.body()!!.photos!!.photo!!)

                }
            }

            override fun onFailure(call: Call<Model>, t: Throwable) {
                Log.d("Failure", t.message)
            }
        })
    }

    private fun showProfile() {

        frameLayout!!.visibility = View.INVISIBLE
        constraintLayout!!.visibility = View.VISIBLE
    }

    private fun getRecent(retrofitClient: RetrofitClient, currentPageNumber: Int) {
        imageAdapter!!.clear()
        progressBar!!.visibility = View.GONE
        frameLayout!!.visibility = View.VISIBLE
        constraintLayout!!.visibility = View.INVISIBLE
        val call = retrofitClient.getPhotosByPage(currentPageNumber.toString())

        call.enqueue(object : Callback<Model> {
            override fun onResponse(call: Call<Model>, response: Response<Model>) {
                if (response.isSuccessful) {
                    for (models in response.body()!!.photos!!.photo!!) {
                        Log.d("Response", models.urlS)
                    }

                    imageAdapter!!.addAll(response.body()!!.photos!!.photo!!)
                    addToSQL(response.body()!!.photos!!.photo!!)
                }
            }

            override fun onFailure(call: Call<Model>, t: Throwable) {
                Log.d("Failure", t.message)
            }
        })
    }

    private fun addToSQL(photos: List<Photo>) {
        val repo = PhotoRepo(application)
        for (photo in photos) {
            repo.insert(photo)
        }

    }

    private fun checkConnection() {
        val repo = PhotoRepo(application)
        if (ConnectivityReceiver.isConnected()) {
            getRecent(retrofitClient, currentPageNumber)
            Toast.makeText(applicationContext, "Internet Available", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Internet Not Available", Toast.LENGTH_SHORT).show()
            imageAdapter?.clear()
            val photo: LiveData<List<Photo>> = repo.getAllPhotos()
            photo.observe(this, Observer { photos ->
                photos?.let { imageAdapter?.addAll(it) }
            })
        }
    }

    companion object {
        private const val PAGE_START = 1
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        val connectivityReceiver = ConnectivityReceiver()
        registerReceiver(connectivityReceiver,intentFilter)
        MyApplication.getInstance().setConnectivityListener(this)
    }
    override fun onNetworkConnectionChanged(isConnected: Boolean) {

        Log.d("Connection", "Check connection triggered")
        checkConnection()
    }
}

