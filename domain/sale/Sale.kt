package com.ionob.pos.domain.sale

import java.math.BigDecimal
import java.util.ArrayList

import com.ionob.pos.domain.inventory.LineItem
import com.ionob.pos.domain.inventory.Product

/**
 * Sale represents sale operation.
 *
 * @author Ionob Team
 */
open class Sale
/**
 * Constructs a new Sale.
 * @param id ID of this Sale.
 * @param startTime start time of this Sale.
 * @param endTime end time of this Sale.
 * @param status status of this Sale.
 * @param items list of LineItem in this Sale.
 */
@JvmOverloads constructor(var id: Int, var startTime: String, var endTime: String = startTime, var status: String ?= ""
                          , private var items: MutableList<LineItem> = ArrayList()) {
     var customerCode: String? = null
     var customerId: Int? = null
     var salesRep: String? = null
     var invoiceDate: String? = null
     var invoiceNo: String? = null
     var profileId: Int? = null
     var subTotal: BigDecimal? = null
     var grandTotal: BigDecimal? = null
     var discountAmountt: BigDecimal? = null
    var  lineDiscount: BigDecimal? = null
     var isChecked: Boolean? = null
     var bpLocationId: Int? = null
     var remoteRecordId: Int? = null
     var isPurchase: Boolean? = null
     var priceListId:Int? = null
     var roundOff: BigDecimal? = null
     var errorMessage: String? = null
     var amountPaid: BigDecimal? = BigDecimal.ZERO
     var paymentType: String? =null
    var  referenceNumber: String? =null
    /**
     * Returns list of LineItem in this Sale.
     * @return list of LineItem in this Sale.
     */
    val allLineItem: List<LineItem>
        get() = items

    /**
     * Returns the total price of this Sale.
     * @return the total price of this Sale.
     */
    open val total: BigDecimal
        get() {
            var amount = BigDecimal.ZERO
            for (lineItem in items) {
                amount = amount.add(lineItem.totalAmount)
            }
            return amount
        }

    open val totalDiscount: BigDecimal
        get() {
            var discount = BigDecimal.ZERO
            for (lineItem in items) {
                discount = discount.add(lineItem.discount)
            }
            return discount
        }
    open val totaltax: BigDecimal
        get() {
            var taxwholeamount = BigDecimal.ZERO
            for (lineItem in items) {
                taxwholeamount = taxwholeamount.add(lineItem.totalTax)
            }
            return taxwholeamount
        }

    /**
     * Returns the total quantity of this Sale.
     * @return the total quantity of this Sale.
     */
    open val orders: BigDecimal
        get() {
            var orderCount = BigDecimal.ZERO
            for (lineItem in items) {
                orderCount = orderCount.add(lineItem.quantity)
            }
            return orderCount
        }

    /**
     * Add Product to Sale.
     * @param product product to be added.
     * @param quantity quantity of product that added.
     * @return LineItem of Sale that just added.
     */
    fun addLineItem(product: Product, quantity: BigDecimal): LineItem {

        for (lineItem in items) {
            if (lineItem.product!!.productId == product.productId) {
                lineItem.quantity=(quantity)
                return lineItem
            }
        }

        val lineItem = LineItem(product, quantity)
        items.add(lineItem)
        return lineItem
    }

    fun size(): Int {
        return items.size
    }

    /**
     * Returns a LineItem with specific index.
     * @param index of specific LineItem.
     * @return a LineItem with specific index.
     */
    fun getLineItemAt(index: Int): LineItem? {
        return if (index >= 0 && index < items.size) items[index] else null
    }

    /**
     * Returns the description of this Sale in Map format.
     * @return the description of this Sale in Map format.
     */
//    fun toMap(): Map<String, String> {
//        val map = HashMap<String, String>()
//        map["id"] = id.toString() + ""
//        map["startTime"] = startTime
//        map["endTime"] = endTime
//        map["status"] = status
//        map["total"] = total + ""
//        map["orders"] = orders + ""
//
//        return map
//    }

    /**
     * Removes LineItem from Sale.
     * @param lineItem lineItem to be removed.
     */
    fun removeItem(lineItem: LineItem) {
        items.remove(lineItem)
    }

}