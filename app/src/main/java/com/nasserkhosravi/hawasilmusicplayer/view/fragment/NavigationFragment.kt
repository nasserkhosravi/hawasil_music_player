package com.nasserkhosravi.hawasilmusicplayer.view.fragment

import android.os.Bundle
import android.view.View
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment
import com.nasserkhosravi.hawasilmusicplayer.R
import kotlinx.android.synthetic.main.fragment_navigation.*

class NavigationFragment : BaseComponentFragment() {
    override val layoutRes: Int
        get() = R.layout.fragment_navigation

    var listener: BubbleNavigationChangeListener? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation.setNavigationChangeListener(listener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomNavigation.setNavigationChangeListener(null)
    }

    companion object {
        fun newInstance(): NavigationFragment {
            return NavigationFragment()
        }

        fun tag(): String {
            return NavigationFragment::class.java.simpleName
        }
    }
}