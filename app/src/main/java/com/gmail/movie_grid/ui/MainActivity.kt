package com.gmail.movie_grid.ui

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Parcelable
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
import com.gmail.movie_grid.data.viewModel.SavedStateViewModel
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
    private lateinit var adapter: ImageGalleryAdapter
    private var isFirstStart = true
    private var positionIndex = -1
    private var topView = 0
    private var listState: Parcelable? = null
    private var listResult: MutableList<Result>? = null
    private var isScreenOrientationChange = false
    private lateinit var savedStateViewModel: SavedStateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        savedStateViewModel = ViewModelProvider(this).get(SavedStateViewModel::class.java)

        isFirstStart = savedInstanceState?.getBoolean(IS_FIRST_START) ?: true
        isScreenOrientationChange =
            savedInstanceState?.getBoolean("IS_SCREEN_ORIENTATION_CHANGE") ?: false

        currentPage = savedInstanceState?.getInt(CURRENT_PAGE) ?: PAGE_START
        totalPage = savedInstanceState?.getInt(TOTAL_PAGE) ?: 0

        listState = savedStateViewModel.getListState()


        filmViewModel = ViewModelProvider(this).get(FilmsViewModel::class.java)


        when (hasConnection(this)) {
            false -> {
                filmViewModel.allResponses.observe(this, Observer { response ->
                    response.forEach {
                        if (adapter.films != it.results) {
                            adapter.addItems(it.results)
                        }
                    }

                })
            }
        }

        recyclerView.layoutManager?.onRestoreInstanceState(listState)
        layoutManager = if (getScreenOrientation() == PORTRAIT_ORIENTATION) GridLayoutManager(
            this,
            2
        ) else GridLayoutManager(this, 4)
        setOnScrollListener(layoutManager)
        recyclerView.layoutManager = layoutManager
        swipeRefresh.setOnRefreshListener(this)
        recyclerView.setHasFixedSize(true)





        listResult = savedStateViewModel.getFilms() ?: ArrayList()
        adapter = ImageGalleryAdapter(this, listResult as MutableList<Result>)
        recyclerView.adapter = adapter
        when (isFirstStart) {
            true -> {
                doApiCall()
                isFirstStart = false
            }
        }
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

                    totalPage = result!!.totalPages

                    if (currentPage != 1) adapter.removeLoading()
                    isScreenOrientationChange = false
                    filmViewModel.insert(result)
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

    override fun onPause() {
        super.onPause()
        listState = recyclerView.layoutManager?.onSaveInstanceState()
        savedStateViewModel.saveListState(listState)
        savedStateViewModel.saveFilms(adapter.films)
    }

    private fun makeErrorToast() {
        Toast.makeText(this, "Error internet", Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        when (hasConnection(this)) {
            true -> {
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

    private fun setOnScrollListener(layoutManager: RecyclerView.LayoutManager) {
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
        private const val CURRENT_PAGE = "currentPage"
        private const val IS_LAST_PAGE = "isLastPage"
        private const val TOTAL_PAGE = "totalPage"
        private const val IS_LOADING = "isLoading"
        private const val IS_FIRST_START = "isFirstStart"
        private const val POSITION_INDEX = "positionIndex"
        private const val TOP_VIEW = "topView"
    }


    override fun onSaveInstanceState(
        outState: Bundle
    ) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_LAST_PAGE, isLastPage)
        outState.putBoolean(IS_LOADING, isLoading)
        outState.putBoolean(IS_FIRST_START, isFirstStart)
        outState.putBoolean("IS_SCREEN_ORIENTATION_CHANGE", isScreenOrientationChange)

        outState.putInt(CURRENT_PAGE, currentPage)
        outState.putInt(TOTAL_PAGE, totalPage)
        outState.putInt(POSITION_INDEX, positionIndex)
        outState.putInt(TOP_VIEW, topView)
    }

    private fun hasConnection(context: Context): Boolean {
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