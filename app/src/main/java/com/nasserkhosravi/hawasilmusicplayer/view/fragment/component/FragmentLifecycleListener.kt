package com.nasserkhosravi.hawasilmusicplayer.view.fragment.component

interface FragmentLifecycleListener {
    fun onCreateViewFragment() {}
    fun onViewCreatedFragment() {}
    fun onStartFragment() {}
    fun onResumeFragment() {}
    fun onPauseFragment() {}
    fun onStopFragment() {}
    fun onDestroyViewFragment() {}
    fun onDestroyFragment() {}
}