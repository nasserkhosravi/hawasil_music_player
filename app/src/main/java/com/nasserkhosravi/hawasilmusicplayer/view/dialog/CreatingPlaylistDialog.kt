package com.nasserkhosravi.hawasilmusicplayer.view.dialog

import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentDialog
import com.nasserkhosravi.hawasilmusicplayer.R
import kotlinx.android.synthetic.main.dialog_creating_playlist.*

class CreatingPlaylistDialog : BaseComponentDialog() {

    override val layoutRes: Int
        get() = R.layout.dialog_creating_playlist

    var onClickListener: View.OnClickListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onClickListener?.let {
            btnConfirm.setOnClickListener(it)
            btnCancel.setOnClickListener(it)
        }
    }

    fun getEditText(): EditText? = edText

    override fun onDestroyView() {
        super.onDestroyView()
        btnConfirm.setOnClickListener(null)
        btnCancel.setOnClickListener(null)
    }

    companion object {

        fun tag(): String {
            return CreatingPlaylistDialog::class.java.simpleName
        }

        fun newInstance(): CreatingPlaylistDialog {
            return CreatingPlaylistDialog()
        }
    }
}