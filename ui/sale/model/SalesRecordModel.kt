package com.ionob.pos.ui.sale.model


import java.math.BigDecimal
import java.util.Date

data class SalesRecordModel(var id:Int) {

    var customerName: String? = null
    var customerCode: String?=null
    var customerId: Int = 0
    var salesRep: String?=null
    var invoiceDate: Date?=null
    var invoiceNo: String?=null
    var profileId: Int = 0
    var status: String?=null
    var subTotal: BigDecimal?= BigDecimal.ZERO
    var grandTotal: BigDecimal?=BigDecimal.ZERO
    var discountAmountt: BigDecimal?=BigDecimal.ZERO
    @Transient
    var isChecked: Boolean = false
    var bpLocationId: Int = 0
    var remoteRecordId: Int = 0
    var isPurchase: Boolean = false
    var priceListId: Int = 0
    var roundOff: BigDecimal?= BigDecimal.ZERO
    var errorMessage: String? = null


}
