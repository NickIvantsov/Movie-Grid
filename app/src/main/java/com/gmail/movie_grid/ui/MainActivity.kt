package com.gmail.movie_grid.ui

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.gmail.movie_grid.R
import com.gmail.movie_grid.adapter.ImageGalleryAdapter
import com.gmail.movie_grid.adapter.PaginationListener
import com.gmail.movie_grid.adapter.PaginationListener.Companion.PAGE_START
import com.gmail.movie_grid.data.viewModel.FilmsViewModel
import com.gmail.movie_grid.model.Result
import com.gmail.movie_grid.net.NetworkService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity(),
    SwipeRefreshLayout.OnRefreshListener {
    private val disposable = CompositeDisposable()
    private lateinit var filmViewModel: FilmsViewModel
    private lateinit var layoutManager: RecyclerView.LayoutManager
    @BindView(R.id.swipeRefresh)
    lateinit var swipeRefresh: SwipeRefreshLayout
    @BindView(R.id.rv_images)
    lateinit var recyclerView: RecyclerView
    private var currentPage: Int = PAGE_START
    private var isLastPage = false
    private var totalPage = 0
    private var isLoading = false
    var itemCount = 0
    lateinit var adapter: ImageGalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        filmViewModel = ViewModelProvider(this).get(FilmsViewModel::class.java)

        filmViewModel.allResponses.observe(this, Observer { response ->
            response.forEach {
                adapter.addItems(it.results)
            }
        })
        swipeRefresh.setOnRefreshListener(this)

        layoutManager = GridLayoutManager(this, 2)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager


        adapter = ImageGalleryAdapter(this, ArrayList<Result>() as MutableList<Result>)
        recyclerView.adapter = adapter

        doApiCall()
    }

    fun doApiCall() {
        Log.d(TAG, "current page = $currentPage")
        disposable.add(
            NetworkService.getJSONApi().getPostWithID(currentPage)!!.observeOn(
                AndroidSchedulers.mainThread()
            )
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    adapter.localResponse = result
                    filmViewModel.insert(result)
                    totalPage = result!!.totalPages

                    if (currentPage != 1) adapter.removeLoading()
                    adapter.addItems(result.results)
                    swipeRefresh.isRefreshing = false
                    // check weather is last page or not
                    when {
                        currentPage < totalPage -> {
                            adapter.addLoading()
                        }
                        else -> {
                            isLastPage = true
                        }
                    }
                    isLoading = false
                }, { error ->
                    makeErrorToast()
                    isLoading = false
                    swipeRefresh.isRefreshing = false
                    error.printStackTrace()
                })
        )


    }

    private fun makeErrorToast() {
        Toast.makeText(this, "Error internet", Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        when (hasConnection(this)) {
            true -> {
                itemCount = 0
                currentPage = PAGE_START
                isLastPage = false
                adapter.clear()
                doApiCall()
            }
            else -> {
                makeErrorToast()
                swipeRefresh.isRefreshing = false
                isLoading = false
            }
        }

    }

    override fun onStop() {
        super.onStop()
        // clear all the subscription
        disposable.clear()
    }

    override fun onResume() {
        super.onResume()
        when (getScreenOrientation()) {
            PORTRAIT_ORIENTATION -> {
                layoutManager = GridLayoutManager(this, 2)
                setOnScrollListener(layoutManager)
            }
            LANDSCAPE_ORIENTATION -> {
                layoutManager = GridLayoutManager(this, 4)
                setOnScrollListener(layoutManager)
            }
        }
    }

    private fun setOnScrollListener(layoutManager: RecyclerView.LayoutManager) {
        recyclerView.layoutManager = layoutManager
        recyclerView.addOnScrollListener(object :
            PaginationListener(layoutManager = layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                doApiCall()
            }

            override fun isLastPage(): Boolean = isLastPage

            override fun isLoading(): Boolean = isLoading
        })
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val PORTRAIT_ORIENTATION = 1
        private const val LANDSCAPE_ORIENTATION = 2
        private const val UNKNOWN = -1
    }

    fun hasConnection(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }
        wifiInfo = cm.activeNetworkInfo
        return wifiInfo != null && wifiInfo.isConnected
    }

    private fun getScreenOrientation(): Int? {
        return if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) PORTRAIT_ORIENTATION else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) LANDSCAPE_ORIENTATION else UNKNOWN
    }

}
