package com.taras_bekhta.doittest.views

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.taras_bekhta.doittest.DoItApp
import com.taras_bekhta.doittest.R
import com.taras_bekhta.doittest.events.NetworkChangedEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


abstract class BaseFragment: Fragment() {
    abstract fun getTitle(): String

    lateinit var parentActivity: MainActivity

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        parentActivity = activity!! as MainActivity
    }

    override fun onResume() {
        super.onResume()
        parentActivity.supportActionBar!!.title = getTitle()
    }

    fun showErrorSnackBar(msg: String, view: View) {
        val snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorError))
        snackBar.show()
    }

    fun showSnackBar(msg: String, view: View) {
        val snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(ContextCompat.getColor(context!!, R.color.green))
        snackBar.show()
    }

    fun hideKeyboard() {
        val imm = parentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = parentActivity.currentFocus
        if (view == null) {
            view = View(parentActivity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun networkChanged(event: NetworkChangedEvent) {
        if(DoItApp.isConnected) {
            showSnackBar(getString(R.string.network_found), parentActivity.window.decorView.rootView)
        } else {
            showErrorSnackBar(getString(R.string.network_lost), parentActivity.window.decorView.rootView)
        }
    }
}