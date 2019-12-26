package com.gmail.movie_grid.ui

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
import com.gmail.movie_grid.R
import com.gmail.movie_grid.data.viewModel.FilmsViewModel
import com.gmail.movie_grid.model.Result
import com.gmail.movie_grid.net.NetworkService
import com.gmail.movie_grid.util.FilmFilter
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

        val id = intent.getIntExtra("Test", 0)
        val page = intent.getIntExtra("PAGE", 0)

        Log.d(TAG, "###### id = $id ######")

        overview.movementMethod = ScrollingMovementMethod()

        backTv.setOnClickListener {
            finish()
        }
        filmViewModel = ViewModelProvider(this).get(FilmsViewModel::class.java)

        filmViewModel.allResponses.observe(this, Observer { response ->
            var i = 0
            response.forEach {
                Log.d(TAG, it.toString())
                System.out.println(" MY COUNT $i ")/*|||DentalFilmActivity:result  = $it ||| results =  ${it.results}|||*/
                i++
                val results: List<Result> = FilmFilter(id).filter(it.results)
                Log.d(TAG, "||||| RESULT LIST = $results |||||||")
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
                            .dontTransform()

                        Glide.with(this)
                            .load(bgPath)
                            .placeholder(R.drawable.ic_launcher_background)
                            .apply(options)
                            .apply(bitmapTransform(BlurTransformation(25, 3)))
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    Log.e(TAG, "Error bgPath loading image", e)
                                    return false // important to return false so the error placeholder can be placed
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }


                            })
                            .into(imageView)

                        Glide.with(this)
                            .load(posterPath)
                            .placeholder(R.drawable.ic_launcher_background)
                            .apply(options)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    Log.e(TAG, "Error bgPath loading image", e)
                                    return false // important to return false so the error placeholder can be placed
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }

                            })
                            .into(this.posterPath)
                    }
                }
            }

        })
    }

    companion object {
        private val TAG = DentalFilmActivity::class.java.simpleName
    }
}
