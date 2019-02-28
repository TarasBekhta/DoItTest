package com.taras_bekhta.doittest

import android.app.Application
import android.content.IntentFilter
import android.support.v7.app.AppCompatDelegate
import com.taras_bekhta.doittest.recievers.NetworkChangeBroadcastReciever

class DoItApp: Application() {
    companion object {
        var isConnected: Boolean = true
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(NetworkChangeBroadcastReciever(), IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}