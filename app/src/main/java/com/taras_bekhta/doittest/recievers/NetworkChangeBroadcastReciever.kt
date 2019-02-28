package com.taras_bekhta.doittest.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.taras_bekhta.doittest.DoItApp
import com.taras_bekhta.doittest.events.NetworkChangedEvent
import org.greenrobot.eventbus.EventBus


class NetworkChangeBroadcastReciever : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val connMgr = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connMgr.activeNetworkInfo

        val prevValue = DoItApp.isConnected
        DoItApp.isConnected = netInfo != null && netInfo.isConnected

        if(prevValue != DoItApp.isConnected) {
            EventBus.getDefault().post(NetworkChangedEvent())
        }
    }
}