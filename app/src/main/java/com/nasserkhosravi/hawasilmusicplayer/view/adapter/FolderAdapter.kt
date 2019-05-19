package com.nasserkhosravi.hawasilmusicplayer.view.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.appcomponent.view.adapter.ClickListenerBinder
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.model.FlatFolderModel

class FolderAdapter : BaseComponentAdapter<FlatFolderModel>() {

    override val layoutRes: Int
        get() = R.layout.row_folder

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(vh, position)
        vh as ViewHolder
        vh.tvTitle.text = items!![position].name
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)

        @ClickListenerBinder
        val imgMore = view.findViewById<ImageView>(R.id.imgMore)
    }
}