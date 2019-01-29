package com.ionob.pos.ui

import android.annotation.SuppressLint
import java.util.Locale

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button

import com.ionob.pos.R
import com.ionob.pos.domain.DateTimeStrategy
import com.ionob.pos.domain.LanguageController
import com.ionob.pos.domain.inventory.Inventory
import com.ionob.pos.domain.sale.Register
import com.ionob.pos.domain.sale.SaleLedger
import com.ionob.pos.db.AndroidDatabase
import com.ionob.pos.db.DatabaseExecutor
import com.ionob.pos.db.inventory.InventoryDaoAndroid
import com.ionob.pos.db.sale.SaleDaoAndroid
import com.ionob.pos.domain.sale.SalesRcord
import com.ionob.pos.ui.sale.ActivityMainMenu

/**
 * This is the first activity page, core-app and database created here.
 * Dependency injection happens here.
 *
 * @author Ionob Team
 */
class SplashScreenActivity : Activity() {
    private var goButton: Button? = null
    private var gone: Boolean = false

    /**
     * Loads database and DAO.
     */
    private fun initiateCoreApp() {
        val database = AndroidDatabase(this)
        val inventoryDao = InventoryDaoAndroid(database)
        val saleDao = SaleDaoAndroid(database)
        DatabaseExecutor.setDatabase(database)
        LanguageController.setDatabase(database)
        Inventory.setInventoryDao(inventoryDao)
        Register.setSaleDao(saleDao)
        SaleLedger.setSaleDao(saleDao)
        SalesRcord.setSaleDao(saleDao)
        DateTimeStrategy.setLocale("th", "TH")
        setLanguage(LanguageController.getInstance().language)

        Log.d("Core App", "INITIATE")
    }

    /**
     * Set language.
     * @param localeString
     */
    private fun setLanguage(localeString: String) {
        val locale = Locale(localeString)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config,
                baseContext.resources.displayMetrics)
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        initiateUI(savedInstanceState)
        initiateCoreApp()
    }

    /**
     * Go.
     */
    private fun go() {
        gone = true
        val newActivity = Intent(this@SplashScreenActivity,
                ActivityMainMenu::class.java)
        startActivity(newActivity)
        this@SplashScreenActivity.finish()
    }

    /**
     * Initiate this UI.
     * @param savedInstanceState
     */
    private fun initiateUI(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_splashscreen)
      //  goButton = findViewById<View>(R.id.goButton) as Button
        //goButton!!.setOnClickListener { go() }
        Handler().postDelayed({ if (!gone) go() }, SPLASH_TIMEOUT)
    }

    companion object {

        val POS_VERSION = "Mobile POS 0.8"
        private val SPLASH_TIMEOUT: Long = 2000
    }

}