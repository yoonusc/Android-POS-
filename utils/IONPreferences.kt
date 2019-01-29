package com.ionob.pos.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment

import com.ionob.pos.R
import com.ionob.pos.app.AppController

object IONPreferences {



    val sharedAppPrefs: SharedPreferences
        get() = AppController.instance!!.getSharedPreferences(AppController.instance!!.getString(R.string.app_name), Context.MODE_PRIVATE)


    fun getSelectedCustomerId(): Int? {
        return sharedAppPrefs.getInt(Constance.SELECTED_CUSTOMER,Constance.UNDIFINED_CUSTOMER)
    }

    fun setSelectedCustomerId(selectedCustomerId:Int?) {
         sharedAppPrefs.edit().putInt(Constance.SELECTED_CUSTOMER,selectedCustomerId?:Constance.UNDIFINED_CUSTOMER).commit()
    }

}
