package com.example.quartzevents.auth

//On November 25, 2026 at 12:50am, searched "Android Studio get device id" in Google.
//Lines 6-13 in this file was provided by Google AI Overview after the search.

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

@SuppressLint("HardwareIds")
fun getDeviceId(context: Context): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}
