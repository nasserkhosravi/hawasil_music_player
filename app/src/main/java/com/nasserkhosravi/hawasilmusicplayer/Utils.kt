package com.nasserkhosravi.hawasilmusicplayer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.text.format.Formatter
import android.util.Log
import androidx.annotation.DimenRes
import androidx.core.app.ActivityCompat
import com.nasserkhosravi.appcomponent.AppContext
import java.io.File
import java.lang.reflect.InvocationTargetException
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

object KFileUtils {
    const val LOG_TAG = "FileUtils"

    fun tag(): String {
        return KFileUtils::class.java.simpleName
    }

    fun getListOfVolumes() {
        val storageManager = AppContext.get().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        try {
            val volumes = storageManager.javaClass.getMethod("getVolumePaths", *arrayOfNulls(0)).invoke(
                storageManager,
                *arrayOfNulls(0)
            ) as Array<String>
            for (i in 0 until volumes.size) {
                Log.d(tag(), volumes[i])
                val file = File(volumes[i])
                Log.d(tag(), "Free Space:" + format(file.freeSpace) + "Total Space:" + format(file.totalSpace))
                Log.d(tag(), "Status of the Media:" + Environment.getExternalStorageState(file))
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }

    }

    private fun format(bytes: Long): String {
        return Formatter.formatShortFileSize(AppContext.get(), bytes)
    }

    //not tested
    fun getListVolumesN() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val storageManager = AppContext.get().getSystemService(Context.STORAGE_SERVICE) as StorageManager
            storageManager.storageVolumes.forEach {
                Log.d(tag(), "${it.getDescription(AppContext.get())}: ")
            }
        }
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