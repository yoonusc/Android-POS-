package com.ionob.pos.app

import android.preference.Preference
import android.support.multidex.MultiDexApplication
import com.ionob.pos.db.AndroidDatabase
import com.ionob.pos.ui.sale.model.CustomerModel

import java.util.Date


class AppController : MultiDexApplication() {

    private val TAG = javaClass.simpleName

    val dbPath: String
        get() = getDatabasePath(AndroidDatabase.DATABASE_NAME).path

    fun getSelectedSalesRep(): CustomerModel? {

        return null
    }

    fun setSelectedSalsesRep(salesRep: CustomerModel) {
        //Preference.setSelectedProfile(selectedProfile.getId())
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }



    companion object {
        @get:Synchronized
        var instance: AppController? = null


    }

}

