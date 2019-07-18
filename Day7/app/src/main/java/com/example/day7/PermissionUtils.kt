package com.example.day7

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import permissions.dispatcher.PermissionRequest

object PermissionUtils {
    fun showRationalDialog(
        activity: AppCompatActivity,
        permissionRequest: PermissionRequest,
        @StringRes message: Int,
        @StringRes positiveButton: Int,
        @StringRes negativeButton: Int
    ) {
        AlertDialog.Builder(activity)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ -> permissionRequest.proceed() }
            .setNegativeButton(negativeButton) { _, _ -> permissionRequest.cancel() }
            .show()
    }

    fun showNVDialog(
        activity: AppCompatActivity,
        @StringRes message: Int,
        @StringRes positiveButton: Int,
        @StringRes negativeButton: Int
    ) {
        AlertDialog.Builder(activity)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, which ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + activity.packageName)
                activity.startActivity(intent)
            }
            .setNegativeButton(negativeButton, null)
            .show()
    }
}