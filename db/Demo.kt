package com.ionob.pos.db

import android.app.Application
import java.io.BufferedReader
import java.io.InputStreamReader

import android.content.Context
import android.provider.Telephony
import android.util.Log

import com.ionob.pos.R
import com.ionob.pos.db.Tax.Tax
import com.ionob.pos.db.Tax.TaxDbo
import com.ionob.pos.db.customer.CustomerDbao
import com.ionob.pos.db.inventory.CategoryDbo
import com.ionob.pos.domain.inventory.Category
import com.ionob.pos.domain.inventory.Inventory
import com.ionob.pos.ui.sale.model.CustomerModel
import java.math.BigDecimal

/**
 * Reads a demo products from CSV in res/raw/
 *
 * @author Refresh yoonus
 */
object Demo {

    /**
     * Adds the demo product to inventory.
     * @param context The current stage of the application.
     */
    fun testProduct(context: Context) {
        val instream = context.resources.openRawResource(R.raw.products)
        val reader = BufferedReader(InputStreamReader(instream))
        var line: String
        try {
            val catalog = Inventory.getInstance().getProductCatalog()

            var count = 1
            var catId=1000;
            while (count<15) {
                line = reader.readLine()
                val contents = line.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                Log.d("Demo", contents[0] + ":" + contents[1] + ": " + contents[2])
                catId++
                catalog.addProduct(contents[1],contents[0].toInt(), contents[0], (contents[2]).toString().toBigDecimal(),"kg", BigDecimal.TEN,catId,"",1,10.toDouble(),true,4.toBigDecimal())
                if(count%3==0)
                {
                    catId++
                }
                count++
                if(count==15)
                {
                    break
                }

            }
        }catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun testaddTax(context: Context) {

        try {
            val Taxs = TaxDbo.getInstance(context)
            var count = 1;
            var TaxId=0

            while (count <4) {
                TaxId++
                var demmuyTax= Tax(TaxId,"demoTax"+1+TaxId+"%",(10+TaxId).toDouble(),false)
                var instatus:Int=  Taxs.addTax(demmuyTax)
                Log.e("instatus",instatus.toString())

                TaxId= count++

            }
        }catch (e: Exception) {
            e.printStackTrace()
        }

    }



    fun testCategory(context: Context) {
        try {
            val category = CategoryDbo.getInstance(context)
            var count = 1;
            var catId=1000
            while (count <10) {

                var demmuyCategry=Category(0,"demoCate"+catId,catId,"")
                category.addCategory(demmuyCategry)
                catId=
                        count++
                if(count==15)
                {
                    break
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun testCustomer(context: Context) {
        val instream = context.resources.openRawResource(R.raw.products)
        val reader = BufferedReader(InputStreamReader(instream))
        var line: String
        try {
            val customerDB = CustomerDbao.getInstance(context)
            line = reader.readLine()
            var count = 0
            while (count<14) {
                var customer= CustomerModel()
                customer.customerId=count+1000
                customer.customerName="DemoCustomer"+count
                customer.customerCode="cs"+count
                customer.countryName="India"
                customer.city="calicut"
                customer.address1="abcd"
                customer.address2="abcd"
                customer.email="abcd@gmail.com"
                customer.postalCode="123456789"
                customer.seaquenceNo="000"+count
                customer.isActive=true
                customer.isNew=false
                customer.status= "S"
                customer.poPriceListId=1
                customer.poPriceListId=1
                customer.phone="812993485"
                customer.taxId="15"
                customer.isNoTax=false
                customer.regionName="kerala"
                customer.stateCode="kl"
                customer.isCustomer=true
                customer.isVendor=false
                customer.isEmployee=false
                customer.isSalesRep=false
                customer.countryId=1258
                customer.regionID=12
                customer.profileId=0

                count++
                if(count==15)
                {
                    break
                }
                customerDB.addCustomer(customer)

            }
        }catch (e: Exception) {
            e.printStackTrace()
        }

    }


}
