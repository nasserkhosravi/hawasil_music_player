package com.nasserkhosravi.hawasilmusicplayer.view.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.model.ArtistModel

class ArtistAdapter : BaseComponentAdapter<ArtistModel>() {
    override val layoutRes: Int
        get() = R.layout.row_artist

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(vh, position)
        vh as ViewHolder
        vh.tvTitle.text = items!![position].title
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val imgMore = view.findViewById<ImageView>(R.id.imgMore)
    }
}