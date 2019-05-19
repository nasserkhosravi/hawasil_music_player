package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R

class HawasilFragment : BaseComponentFragment() {

    override val layoutRes: Int
        get() = R.layout.fragment_hawasil

    companion object {
        fun newInstance(): HawasilFragment {
            return HawasilFragment()
        }
    }
}