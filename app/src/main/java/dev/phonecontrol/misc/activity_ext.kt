package dev.phonecontrol.misc

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun Activity.openDefaultAppsSettings() {
    Intent(
        Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS,
    ).also(::startActivity)
}
