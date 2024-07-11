package com.example.warehousetracebilityandroid.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

fun vibrate(context: Context) {
    // Get the Vibrator service
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    // Vibrate for 200 milliseconds
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        // For newer APIs after API 26 (Android 8.0)
//        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
//    } else {
//        // Deprecated in API 26 but necessary for devices running API < 26
//        @Suppress("DEPRECATION")
//        vibrator.vibrate(100)
//    }
}
