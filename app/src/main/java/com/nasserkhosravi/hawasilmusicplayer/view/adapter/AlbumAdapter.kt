package com.nasserkhosravi.hawasilmusicplayer.view.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.adapter.ClickListenerBinder
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.model.AlbumModel

class AlbumAdapter : BaseComponentAdapter<AlbumModel>() {
    override val layoutRes: Int
        get() = R.layout.row_album

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(vh, position)
        vh as ViewHolder
        val model = items!![position]

        vh.tvTitle.text = model.title
        vh.tvArtist.text = model.artist
        if (model.artWorkUri != null) {
            Glide.with(vh.imgArt).load(model.artWorkUri).apply(RequestOptions.circleCropTransform()).into(vh.imgArt)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgArt = view.findViewById<ImageView>(R.id.imgThumb)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvArtist: TextView = view.findViewById(R.id.tvArtist)

        @ClickListenerBinder
        val imgMore = view.findViewById<ImageView>(R.id.imgMore)
    }
}