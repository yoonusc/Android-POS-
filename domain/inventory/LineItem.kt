package com.ionob.pos.domain.inventory

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * LineItem of Sale.
 *
 * @author YOONUS
 */
class LineItem
/**
 * Constructs a new LineItem.
 * @param id ID of this LineItem, This value should be assigned from database.
 * @param product product of this LineItem.
 * @param quantity product quantity of this LineItem.
 * @param unitPriceAtSale unit price at sale time. default is price from ProductCatalog.
 * @param discount line discount.
 * @param taxAmount line tax amount.
 */
(var id: Int, val product: Product?, var quantity: BigDecimal, private var unitPriceAtSale: BigDecimal?
 , var discount:BigDecimal= BigDecimal.ZERO , var taxAmount:BigDecimal= BigDecimal.ZERO) {


    var trim= DecimalFormat("00.00")

    var totalAmount: BigDecimal?= BigDecimal.ZERO
        get() = unitPriceAtSale?.multiply(quantity)

    var netAmount: BigDecimal?= BigDecimal.ZERO
        get() = this.totalAmount?.subtract(this.discount)

    val priceAtSale: BigDecimal?
        get() = unitPriceAtSale

    var totalTax: BigDecimal? = BigDecimal.ZERO
        get() =taxamount()

    var totalpercent: BigDecimal? = BigDecimal.ZERO
        get() =product!!.tax_percent.toBigDecimal()

    constructor(product: Product?, quantity: BigDecimal) : this(UNDEFINED, product, quantity, product?.unitPrice) {}

    /**
     * Adds quantity of product in this LineItem.
     * @param amount amount for add in quantity.
     */
    fun addQuantity(qty: BigDecimal) {
        this.quantity=this.quantity.add( qty)
    }

    fun didcutQuantity(qty: BigDecimal) {
        this.quantity=this.quantity.subtract( qty)
    }


    /**
     * Sets price product of this LineItem.
     * @param unitPriceAtSale price product of this LineItem.
     */
    fun setUnitPriceAtSale(unitPriceAtSale: BigDecimal) {
        this.unitPriceAtSale = unitPriceAtSale
    }



    fun taxamount():BigDecimal?{
        var tax:BigDecimal?= BigDecimal.ZERO

        if (product!!.is_tax_inclusive==false) {
            tax = (((unitPriceAtSale)!!.multiply(product!!.tax_percent.toBigDecimal())).divide(100.toBigDecimal())).multiply(quantity)
        }else{
           // tax excluded price  = item price / ((tax rate /100) + 1 )
            var taxExcludedPrice=product?.unitPrice/((product?.tax_percent.toBigDecimal().divide(100.toBigDecimal()))+BigDecimal.ONE)
            var itemTaxt=product?.unitPrice?.subtract(taxExcludedPrice)
            tax=(itemTaxt).multiply(quantity)
        }
        return tax
    }

    /**
     * Determines whether two objects are equal or not.
     * @return true if Object is a LineItem with same ID ; otherwise false.
     */
    override fun equals(`object`: Any?): Boolean {
        if (`object` == null)
            return false
        if (`object` !is LineItem)
            return false
        val lineItem = `object` as LineItem?
        return lineItem!!.id == this.id
    }

    companion object {

        /**
         * Static value for UNDEFINED ID.
         */
        val UNDEFINED = -1
    }
}
