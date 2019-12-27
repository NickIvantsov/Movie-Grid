package com.gmail.movie_grid.ui

import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager


        adapter = ImageGalleryAdapter(this, ArrayList<Result>() as MutableList<Result>)
        recyclerView.adapter = adapter

        doApiCall()

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
                    Toast.makeText(this, "Error internet", Toast.LENGTH_SHORT).show()
                    isLoading = false
                    swipeRefresh.isRefreshing = false
                    error.printStackTrace()
                })
        )


    }

    override fun onRefresh() {
        itemCount = 0
        currentPage = PAGE_START
        isLastPage = false
        adapter.clear()
        doApiCall()
    }

    override fun onStop() {
        super.onStop()
        // clear all the subscription
        disposable.clear()
    }


    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

}
