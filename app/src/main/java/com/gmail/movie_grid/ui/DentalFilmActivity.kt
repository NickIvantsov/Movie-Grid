package com.gmail.movie_grid.ui

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.Target
import com.gmail.movie_grid.R
import com.gmail.movie_grid.adapter.ImageGalleryAdapter.Companion.ID
import com.gmail.movie_grid.data.viewModel.FilmsViewModel
import com.gmail.movie_grid.model.Result
import com.gmail.movie_grid.net.NetworkService
import jp.wasabeef.glide.transformations.BlurTransformation

class DentalFilmActivity : AppCompatActivity() {

    private lateinit var filmViewModel: FilmsViewModel
    @BindView(R.id.back_tv)
    lateinit var backTv: TextView
    @BindView(R.id.name_film)
    lateinit var nameFilm: TextView
    @BindView(R.id.release_date)
    lateinit var releaseDate: TextView
    @BindView(R.id.originalTitle)
    lateinit var originalTitle: TextView
    @BindView(R.id.overview)
    lateinit var overview: TextView
    @BindView(R.id.voteAverage)
    lateinit var voteAverage: TextView
    @BindView(R.id.imageView)
    lateinit var imageView: ImageView
    @BindView(R.id.poster_path)
    lateinit var posterPath: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dental_film)

        ButterKnife.bind(this)

        val intent = intent

        val id = intent.getIntExtra(ID, 0)

        overview.movementMethod = ScrollingMovementMethod()

        backTv.setOnClickListener {
            finish()
        }
        filmViewModel = ViewModelProvider(this).get(FilmsViewModel::class.java)

        filmViewModel.allResponses.observe(this, Observer { response ->

            response.forEach {

                val results: List<Result> = it.results.filter { n -> id == n?.id }

                when {
                    results.isNotEmpty() -> {
                        val result: Result = results[0]

                        var bgPath =
                            "${NetworkService.POSTER_URL}${result.backdropPath}"
                        var posterPath =
                            "${NetworkService.POSTER_URL}${result.posterPath}"
                        when {
                            result.backdropPath.isEmpty() -> {
                                bgPath = posterPath
                            }

                            result.posterPath.isEmpty() -> {
                                posterPath = bgPath
                            }
                        }
                        nameFilm.text = result.title
                        releaseDate.text = result.releaseDate
                        originalTitle.text = result.originalTitle
                        overview.text = result.overview
                        voteAverage.text = result.voteAverage.toString()

                        val options: RequestOptions = RequestOptions()
                            .placeholder(R.drawable.progress_animation)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .priority(Priority.HIGH)
                            .dontAnimate()
                            //.error(R.drawable.ic_error_outline_black_24dp)
                            .dontTransform()

                        displayBg(bgPath, options)

                        displayPoster(posterPath, options)
                    }
                }
            }

        })
    }

    private fun displayBg(bgPath: String, options: RequestOptions) {
        Glide.with(this)
            .load(bgPath)
            .placeholder(R.drawable.ic_launcher_background)
            .apply(options)
            .apply(bitmapTransform(BlurTransformation(25, 3)))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e(TAG, "Error bgPath loading image", e)
                    imageView.setBackgroundColor(Color.BLACK)
                    return false // important to return false so the error placeholder can be placed
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }


            }).override(500, 500)
            .into(imageView)
    }

    private fun displayPoster(
        posterPathStr: String,
        options: RequestOptions
    ) {
        Glide.with(this)
            .load(posterPathStr)
            .placeholder(R.drawable.ic_launcher_background)
            .apply(options)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e(TAG, "Error bgPath loading image", e)
                    posterPath.setBackgroundColor(Color.DKGRAY)
                    return false // important to return false so the error placeholder can be placed
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            })
            .into(this.posterPath)
    }

    companion object {
        private val TAG = DentalFilmActivity::class.java.simpleName
    }
}
