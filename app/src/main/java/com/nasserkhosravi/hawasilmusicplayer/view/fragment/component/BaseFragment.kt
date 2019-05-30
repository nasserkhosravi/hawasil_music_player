package com.nasserkhosravi.hawasilmusicplayer.view.fragment.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nasserkhosravi.appcomponent.view.fragment.BaseComponentFragment

abstract class BaseFragment : BaseComponentFragment() {
    var lifecycleListener: FragmentLifecycleListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        lifecycleListener?.onCreateViewFragment()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleListener?.onViewCreatedFragment()
    }

    override fun onResume() {
        super.onResume()
        lifecycleListener?.onResumeFragment()
    }

    override fun onStart() {
        super.onStart()
        lifecycleListener?.onStartFragment()
    }

    override fun onPause() {
        super.onPause()
        lifecycleListener?.onPauseFragment()
    }

    override fun onStop() {
        super.onStop()
        lifecycleListener?.onStopFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleListener?.onDestroyViewFragment()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleListener?.onDestroyFragment()
    }
}