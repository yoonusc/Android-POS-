package com.ionob.pos.ui.sale.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.ArrayList

/**ionob*/

class CustomerModel  ():Serializable

{


        var id: Int = 0
        var customerName: String?=null
        var locationId: Int = 0
        var location: String? = null
        var customerId: Int = 0
        var customerCode: String? = null
        var email: String?=null
        var countryName: String?=null
        var city: String?=null
        var postalCode: String?=null
        var address1: String?=null
        var address2: String?=null
        var profileId: Int = 0
        var seaquenceNo: String? = null
        var isSelected:Boolean = false
        var isActive: Boolean = false
        var isNew: Boolean = false
        var status: String?=null
        var poPriceListId: Int = 0
        var soPriceListId: Int = 0
        var phone: String? = null
        var taxId: String?=null
        var isNoTax: Boolean = false
        var regionName: String?=null
        var stateCode: String?=null
        var isChecked: Boolean = false
        var isCustomer: Boolean = false
        var isVendor: Boolean = false
        var isEmployee: Boolean = false
        var isSalesRep: Boolean = false
        var bpGroupId: Int = 0
        var countryId: Int = 0
        var regionID: Int = 0


    val title: String
        get() = customerName +
                (if (customerCode != null) " ($customerCode)" else "") +
                if (location != null) "\n" + location!! else ""

    fun setIsChecked(isChecked: Boolean): Boolean {
        this.isChecked = isChecked
        return isChecked
    }

    fun getIsChecked(): Boolean {
        return isChecked
    }

    fun getName(): String? {
        return customerName
    }

    fun setName(name: String) {
        this.customerName = name
    }

      fun getRegionId(): Int {
        return regionID
    }

    fun setRegionId(regionId: Int) {
        this.regionID = regionId
    }




    override fun toString(): String {
        return "CustomerModel{" +
                "id='" + id + '\''.toString() +
                ", name='" + customerName + '\''.toString() +
                ", customerId=" + customerId +
                ", customerCode='" + customerCode + '\''.toString() +
                ", email='" + email + '\''.toString() +
                ", country='" + countryName + '\''.toString() +
                ", city='" + city + '\''.toString() +
                ", postalCode='" + postalCode + '\''.toString() +
                ", address1='" + address1 + '\''.toString() +
                ", address2='" + address2 + '\''.toString() +
                ", profileId=" + profileId +
                ", seaquenceNo=" + seaquenceNo +
                ", isSelected=" + isSelected +
                ", active=" + isActive +
                ", status='" + status + '\''.toString() +
                ", phone='" + phone + '\''.toString() +
                '}'.toString()
    }







    companion object {

    }
}



