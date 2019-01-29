package com.ionob.pos.db.customer

import java.util.ArrayList

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.ionob.pos.db.AndroidDatabase

import com.ionob.pos.domain.inventory.Product
import com.ionob.pos.domain.inventory.ProductLot
import com.ionob.pos.db.Database
import com.ionob.pos.db.DatabaseContents
import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.domain.sale.Register
import com.ionob.pos.ui.sale.model.CustomerModel
import java.math.BigDecimal

/**
 * DAO used by android for Inventory.
 *
 * @author yoonus
 */
class CustomerDbao
/**
 * Constructs InventoryDaoAndroid.
 * @param database database for use in InventoryDaoAndroid.
 */
(private val database: Database) : CustomerDao {

    override val allCustomer: List<CustomerModel>
        get() = getAllCustomer(" WHERE 1 = 1 ")


    override fun addCustomer(customer: CustomerModel): Int {
        val content = ContentValues()
        //content.put("id")
        content.put("customer_id",customer.customerId)
        content.put("customer_name",customer.customerName)
        content.put("customer_code",customer.customerCode)
        content.put("country",customer.countryName)
        content.put("city",customer.city)
        content.put("address1",customer.address1)
        content.put("address2",customer.address2)
        content.put("email",customer.email)
        content.put("postal_code",customer.postalCode)
        content.put("seaquenceNo",customer.seaquenceNo)
        content.put("isActive",customer.isActive)
        content.put("isNew",customer.isNew)
        content.put("status",customer.status)
        content.put("poPriceListId",customer.poPriceListId)
        content.put("soPriceListId",customer.soPriceListId)
        content.put("phone",customer.phone)
        content.put("taxId",customer.taxId)
        content.put("isNoTax",customer.isNoTax)
        content.put("regionName",customer.regionName)
        content.put("stateCode",customer.stateCode)
        content.put("isCustomer",customer.isCustomer)
        content.put("isVendor",customer.isVendor)
        content.put("isEmployee",customer.isEmployee)
        content.put("isSalesRep",customer.isSalesRep)
        content.put("countryId",customer.countryId)
        content.put("regionID",customer.regionID)
        content.put("profile_id",customer.profileId)
        return   database.insert(DatabaseContents.TABLE_CUSTOMER.toString(), content)
    }


    /**
     * Converts list of object to list of product.
     * @param objectList list of object.
     * @return list of product.
     */
    /*  "CREATE TABLE IF NOT EXISTS " + DatabaseContents.TABLE_CUSTOMER + "(" + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                    " customer_id INTEGER , customer_name TEXT , customer_code TEXT , " +
                    " country TEXT , city TEXT , address1 TEXT , address2 TEXT ," +
                    " email TEXT, postal_code TEXT, seaquenceNo TEXT,isActive BOOLEAN,isNew BOOLEAN," +
                    " status TEXT , poPriceListId INTEGER , soPriceListId INTEGER , phone TEXT , " +
                    " taxId  INTEGER , isNoTax BOOLEAN , regionName TEXT , stateCode TEXT , " +
                    " isCustomer  BOOLEAN , isVendor BOOLEAN , isEmployee BOOLEAN , isSalesRep BOOLEAN , " +
                    " countryId  INTEGER ,regionID INTEGER, inprofile_id INTEGER" + ");")*/

    private fun toCustomer(objectList: List<Any>): List<CustomerModel> {
        val list = ArrayList<CustomerModel>()
        for (`object` in objectList) {
            val content = `object` as ContentValues
            var customer=CustomerModel()
            customer.id =content.getAsInteger("_id")
            customer.customerId=content.getAsInteger("customer_id")
            customer.customerName=content.getAsString("customer_name")
            customer.customerCode=content.getAsString("customer_code")
            customer.countryName=content.getAsString("country")
            customer.city=content.getAsString("city")
            customer.address1=content.getAsString("address1")
            customer.address2=content.getAsString("address2")
            customer.email=content.getAsString("email")
            customer.postalCode=content.getAsString("postal_code")
            customer.seaquenceNo=content.getAsString("seaquenceNo")
            customer.isActive=content.getAsBoolean("isActive")
            customer.isNew=content.getAsBoolean("isNew")
            customer.status=content.getAsString("status")
            customer.poPriceListId=content.getAsInteger("poPriceListId")
            customer.poPriceListId=content.getAsInteger("soPriceListId")
            customer.phone=content.getAsString("phone")
            customer.taxId=content.getAsString("taxId")
            customer.isNoTax=content.getAsBoolean("isNoTax")
            customer.regionName=content.getAsString("regionName")
            customer.stateCode=content.getAsString("stateCode")
            customer.isCustomer=content.getAsBoolean("isCustomer")
            customer.isVendor=content.getAsBoolean("isVendor")
            customer.isEmployee=content.getAsBoolean("isEmployee")
            customer.isSalesRep=content.getAsBoolean("isSalesRep")
            customer.countryId=content.getAsInteger("countryId")
            customer.regionID=content.getAsInteger("regionID")
            customer.profileId=content.getAsInteger("inprofile_id")?:0
            list.add(customer)

        }
        return list
    }

    /**
     * Returns list of all products in inventory.
     * @param condition specific condition for getAllProduct.
     * @return list of all products in inventory.
     */
    private fun getAllCustomer(condition: String): List<CustomerModel> {
        val queryString = "SELECT * FROM " + DatabaseContents.TABLE_CUSTOMER.toString() + condition + " ORDER BY customer_name"
        Log.d("query",queryString)
        return toCustomer(database.select(queryString)!!)
    }


    override fun getCustomerByid(customerId: Int): CustomerModel {
        return getAllCustomer(" WHERE customer_id= "+customerId.toString()+"")[0]
    }

    override fun getCustomerByName(name: String): List<CustomerModel> {
        return getAllCustomer(" WHERE customer_name= "+name+"")
    }

    override fun searchCustomer(search: String): List<CustomerModel> {
        val condition = " WHERE name LIKE '%$search%' OR barcode LIKE '%$search%' ;"
        return getAllCustomer(condition)
    }

    override fun editCustomer(customer: CustomerModel?): Boolean {
        val content = ContentValues()
        customer?.let {
            content.put("id", customer.id)
            content.put("customer_id", customer.customerId)
            content.put("customer_name", customer.customerName)
            content.put("customer_code", customer.customerCode)
            content.put("country", customer.countryName)
            content.put("city", customer.city)
            content.put("address1", customer.address1)
            content.put("address2", customer.address2)
            content.put("email", customer.email)
            content.put("postal_code", customer.postalCode)
            content.put("seaquenceNo", customer.seaquenceNo)
            content.put("isActive", customer.isActive)
            content.put("isNew", customer.isNew)
            content.put("status", customer.status)
            content.put("poPriceListId", customer.poPriceListId)
            content.put("soPriceListId", customer.soPriceListId)
            content.put("phone", customer.phone)
            content.put("taxId", customer.taxId)
            content.put("isNoTax", customer.isNoTax)
            content.put("regionName", customer.regionName)
            content.put("stateCode", customer.stateCode)
            content.put("isCustomer", customer.isCustomer)
            content.put("isVendor", customer.isVendor)
            content.put("isEmployee", customer.isEmployee)
            content.put("isSalesRep", customer.isSalesRep)
            content.put("countryId", customer.countryId)
            content.put("regionID", customer.regionID)
            content.put("profile_id", customer.profileId)
            return database.update(DatabaseContents.TABLE_CUSTOMER.toString(), content)
        }
        return false
    }


    override fun deleteall() {
        database.execute("DELETE FROM " + DatabaseContents.TABLE_CUSTOMER)
    }
    companion object {
        private var instance: CustomerDbao? = null


        @Throws(NoDaoSetException::class)
         fun getInstance(context: Context): CustomerDbao {
            if (CustomerDbao.instance == null) CustomerDbao.instance = CustomerDbao(AndroidDatabase(context))
            return CustomerDbao.instance as CustomerDbao
        }
    }

}
