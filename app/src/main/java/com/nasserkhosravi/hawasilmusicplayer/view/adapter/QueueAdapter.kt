package com.nasserkhosravi.hawasilmusicplayer.view.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nasserkhosravi.appcomponent.view.adapter.BaseComponentAdapter
import com.nasserkhosravi.hawasilmusicplayer.MyRes
import com.nasserkhosravi.hawasilmusicplayer.R
import com.nasserkhosravi.hawasilmusicplayer.data.QueueManager
import com.nasserkhosravi.hawasilmusicplayer.data.model.SongModel
import com.nasserkhosravi.hawasilmusicplayer.view.ShadowLayout
import java.io.FileNotFoundException

class QueueAdapter : BaseComponentAdapter<SongModel>() {
    override val layoutRes: Int
        get() = R.layout.row_queue

    private var selectedPosition = -1
    private var defaultArt: Bitmap? = null

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(vh, position)
        vh as ViewHolder
        val model = items!![position]
        vh.tvTitle.text = model.title
        vh.tvArtist.text = model.artist
        vh.tvDuration.text = model.getFormatDuration()
        val art = getArt(model)

        Glide.with(vh.imgThumb).load(art).apply(RequestOptions.circleCropTransform()).into(vh.imgThumb)
        val shadowLayout = vh.itemView as ShadowLayout
        val selectedSong = QueueManager.get().queue?.selected
        if (selectedSong != null && model.id == selectedSong.id) {
            selectedPosition = position
            shadowLayout.shadowColor = getColorRes(R.color.enabled_item)
            shadowLayout.shadowRadius = MyRes.getDim(R.dimen.enabled_shadow_radius)
        } else {
            shadowLayout.shadowColor = getColorRes(R.color.default_shadow)
            shadowLayout.shadowRadius = MyRes.getDim(R.dimen.default_shadow_radius)
        }
    }

    private fun getArt(model: SongModel): Bitmap? {
        return if (model.artUri != null) {
            try {
                MediaStore.Images.Media.getBitmap(ctx.contentResolver, model.artUri!!)
            } catch (e: FileNotFoundException) {
                getDefaultArt(ctx)
            }
        } else {
            getDefaultArt(ctx)
        }
    }

    private fun getDefaultArt(context: Context): Bitmap {
        if (defaultArt == null) {
            defaultArt = BitmapFactory.decodeResource(context.resources, R.drawable.art_default)
        }
        return defaultArt!!
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgThumb = view.findViewById<ImageView>(R.id.imgThumb)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvArtist: TextView = view.findViewById(R.id.tvArtist)
        val tvDuration = view.findViewById<TextView>(R.id.tvDuration)
        val imgMore = view.findViewById<ImageView>(R.id.imgMore)
    }

    fun tag(): String {
        return QueueAdapter::class.java.simpleName
    }

    fun makeThisSelect(position: Int) {
        //notify last selected position to be normal
        notifyItemChanged(selectedPosition)

        //notify new selected position to be special
        this.selectedPosition = position
        notifyItemChanged(position)
    }
}