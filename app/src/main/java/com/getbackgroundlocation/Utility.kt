package com.getbackgroundlocation


import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import android.util.Log

object Utility {
const val TAG  = "Location Service"
    fun isLocationServiceRunning(serviceClass: Class<*>, mActivity: Activity): Boolean {
        val manager: ActivityManager =
            mActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i(TAG, "Running")
                return true
            }
        }
        Log.i(TAG, "Not running")
        return false
    }

}