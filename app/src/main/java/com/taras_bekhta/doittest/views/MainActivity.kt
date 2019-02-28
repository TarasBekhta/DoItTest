package com.taras_bekhta.doittest.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.taras_bekhta.doittest.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var host: NavHostFragment
    var authToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        host = supportFragmentManager.findFragmentById(R.id.navigation_fragment_main) as NavHostFragment
        NavigationUI.setupActionBarWithNavController(this, host.navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        NavigationUI.navigateUp(null, Navigation.findNavController(this, R.id.navigation_fragment_main))
        return true
    }

    override fun onBackPressed() {
        NavigationUI.navigateUp(null, Navigation.findNavController(this, R.id.navigation_fragment_main))
    }
}
