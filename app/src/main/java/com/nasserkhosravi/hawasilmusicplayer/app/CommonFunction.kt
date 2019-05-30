package com.nasserkhosravi.hawasilmusicplayer.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.formatString(@StringRes id: Int, vararg args: Any?): String {
    return String.format(getString(id), *args)
}

fun setOnClickListeners(listener: View.OnClickListener?, vararg views: View) {
    for (view in views) {
        view.setOnClickListener(listener)
    }
}

/**
 * Kotlin Extensions for simpler, easier and fun wway
 * of launching of Activities.
 * https://wajahatkarim.com/2019/03/-launching-activities-in-easier-way-using-kotlin-extensions-/
 */
inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode, options)
}

inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivity(intent, options)
    } else {
        startActivity(intent)
    }
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)