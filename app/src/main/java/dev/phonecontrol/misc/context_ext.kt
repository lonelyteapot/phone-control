package dev.phonecontrol.misc

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Expected to be inside an Activity context")
}
