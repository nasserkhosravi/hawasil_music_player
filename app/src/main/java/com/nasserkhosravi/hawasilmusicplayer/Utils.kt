package com.nasserkhosravi.hawasilmusicplayer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.DimenRes
import androidx.core.app.ActivityCompat
import com.nasserkhosravi.appcomponent.AppContext
import java.util.concurrent.TimeUnit

object FormatUtils {
    fun milliSeconds(duration: Long): String {
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration),
            TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        )
    }

    fun toSecond(p: Long): Long {
        return TimeUnit.MILLISECONDS.toSeconds(p)
    }
}

object PermissionUtils {

    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        var result = true
        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED) {
                result = false
            }
        }
        return result
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        val res = context.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun on_Has_HasNot(value: Boolean, has: () -> Unit, hasNot: () -> Unit) {
        if (value) {
            has()
        } else {
            hasNot()
        }
    }

    fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission),
            requestCode
        )
    }

    fun requestPermission(activity: Activity, requestCode: Int, vararg permission: String) {
        ActivityCompat.requestPermissions(
            activity,
            permission,
            requestCode
        )
    }

    fun requestWritePermission(activity: Activity) {
        requestPermission(
            activity,
            getWritePermissionCode(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    fun requestPhoneState(activity: Activity) {
        requestPermission(activity, Manifest.permission.READ_PHONE_STATE, 10)
    }

    fun requestReadPermission(activity: Activity) {
        requestPermission(
            activity,
            getReadPermissionCode(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }


    fun hasWritePermission(context: Context): Boolean {
        val res = context.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun hasReadPermission(context: Context): Boolean {
        val res = context.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun hasCameraPermission(context: Context): Boolean {
        return hasPermission(context, Manifest.permission.CAMERA)
    }

    fun getWritePermissionCode() = 100

    fun getReadPermissionCode() = 101
}

object MyRes {
    fun getDim(@DimenRes dim: Int): Float {
        return AppContext.get().resources.getDimension(dim)
    }
}

class TimeMeasure {
    private var start = 0L
    private var end = 0L

    fun start() {
        if (start == 0L) {
            start = System.currentTimeMillis()
        }
    }

    fun end() {
        if (end == 0L) {
            end = System.currentTimeMillis()
        }
    }

    fun reset() {
        start = 0
        end = 0
    }

    fun getDiff(): Long {
        return end - start
    }

    fun printDiff(tag: String) {
        Log.d(tag, "${getDiff()}")
    }
}