package com.gmail.movie_grid.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.gmail.movie_grid.R
import com.gmail.movie_grid.model.Response
import com.gmail.movie_grid.model.Result
import com.gmail.movie_grid.net.NetworkService
import com.gmail.movie_grid.ui.DentalFilmActivity


class ImageGalleryAdapter(
    private val mContext: Context,
    val films: MutableList<Result>
) :
    RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>() {
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView: View = inflater.inflate(R.layout.movie_item, parent, false)
        return MyViewHolder(photoView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val spacePhoto = "${NetworkService.POSTER_URL}${mFilms[position]?.posterPath ?: ""}"
        val imageView = holder.mPhotoImageView

        println(spacePhoto)
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.progress_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .error(R.drawable.ic_error_outline_black_24dp)
            .dontTransform()

        if (spacePhoto != NetworkService.POSTER_URL) {
            Glide.with(mContext)
                .load(spacePhoto)
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
                .into(imageView)
        }else{

            Log.e(TAG,"DELETE FAIL EL spacePhoto =  $spacePhoto mFilms = ${mFilms[position]}")
        }

    }

    override fun getItemCount(): Int {
        return mFilms.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == films.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    fun addItems(postItems: List<Result>) {
        val oldSize = films.size
        films.addAll(postItems)
        notifyItemRangeInserted(oldSize, films.size)
    }

    fun addLoading() {
        isLoaderVisible = true
        films.add(Result())
        notifyItemInserted(films.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = films.size - 1
        val item: Result? = getItem(position)
        if (item != null) {
            films.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): Result? {
        return films[position]
    }

    inner class MyViewHolder(itemView: View) : ViewHolder(itemView),
        View.OnClickListener {
        var mPhotoImageView: ImageView = itemView.findViewById<View>(R.id.iv_poster) as ImageView

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val spacePhoto: Result = mFilms[position]
                val intent = Intent(mContext, DentalFilmActivity::class.java)
                intent.putExtra(ID, spacePhoto.id)
                mContext.startActivity(intent)
            }
        }

        init {
            itemView.setOnClickListener(this)
        }


    }

    fun clear() {
        films.clear()
        notifyDataSetChanged()
    }

    private val mFilms: List<Result>

    var localResponse: Response? = null

    init {
        mFilms = films
    }

    companion object {
        const val ID = "ID"
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_NORMAL = 1
        const val TAG = "ImageGalleryAdapter"
    }
}