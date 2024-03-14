package dev.phonecontrol.misc

import android.util.Log

// Tag used for logging
inline val <reified T> T.TAG: String
    get() = T::class.java.simpleName

// Logging helpers
inline fun <reified T> T.logv(message: String) = Log.v(TAG, message)
inline fun <reified T> T.logi(message: String) = Log.i(TAG, message)
inline fun <reified T> T.logw(message: String) = Log.w(TAG, message)
inline fun <reified T> T.logd(message: String) = Log.d(TAG, message)
inline fun <reified T> T.loge(message: String) = Log.e(TAG, message)
