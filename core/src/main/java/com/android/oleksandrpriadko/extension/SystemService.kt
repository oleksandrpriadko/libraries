package com.android.oleksandrpriadko.extension

import android.app.Application
import android.app.DownloadManager
import android.app.NotificationManager
import android.app.job.JobScheduler
import android.content.Context
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.nfc.NfcManager
import android.os.Build
import android.os.PowerManager
import android.os.storage.StorageManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi

fun Context.connectivityManager(): ConnectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

fun Context.displayManager(): DisplayManager? =
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

fun Context.downloadManager(): DownloadManager? =
        getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

fun Context.inputMethodManager(): InputMethodManager? =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Context.jobScheduler(): JobScheduler? =
        getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

fun Context.layoutInflater(): LayoutInflater =
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

fun Context.locationManager(): LocationManager =
        getSystemService(Context.LOCATION_SERVICE) as LocationManager

fun Context.nfcManager(): NfcManager? =
        getSystemService(Context.NFC_SERVICE) as NfcManager

fun Context.notificationManager(): NotificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.powerManager(): PowerManager =
        getSystemService(Context.POWER_SERVICE) as PowerManager

fun Context.sensorManager(): SensorManager =
        getSystemService(Context.SENSOR_SERVICE) as SensorManager

fun Context.storageManager(): StorageManager? =
        getSystemService(Context.STORAGE_SERVICE) as StorageManager

fun Context.wifiManager(): WifiManager =
        applicationContext.getSystemService(Application.WIFI_SERVICE) as WifiManager

fun Context.windowService(): WindowManager =
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
