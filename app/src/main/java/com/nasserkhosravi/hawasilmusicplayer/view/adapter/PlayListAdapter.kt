package com.nasserkhosravi.hawasilmusicplayer.view.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.model.PlayListModel

class PlayListAdapter : BaseComponentAdapter<PlayListModel>() {

    override val layoutRes: Int
        get() = R.layout.row_playlist

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(vh, position)
        vh as ViewHolder
        val model = items!![position]
        vh.tvTitle.text = model.title
        vh.tvCount.text = String.format(getStringRes(R.string.TEMPLATE_song_count), model.members.size)
        val artWorks = model.get4Artworks()
        vh.imgArt.visibility = View.VISIBLE
        if (artWorks.isNotEmpty()) {
            vh.imgArt.setImageURI(artWorks[0].second)
        } else {
            //use default
            vh.imgArt.setImageResource(R.drawable.art_default)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvCount: TextView = view.findViewById(R.id.tvCount)
        val imgArt = view.findViewById<ImageView>(R.id.img1)
        val imgMore = view.findViewById<ImageView>(R.id.imgMore)
    }
}