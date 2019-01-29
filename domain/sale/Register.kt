package com.ionob.pos.domain.sale

import com.ionob.pos.domain.DateTimeStrategy
import com.ionob.pos.domain.inventory.Inventory
import com.ionob.pos.domain.inventory.LineItem
import com.ionob.pos.domain.inventory.Product
import com.ionob.pos.domain.inventory.Stock
import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.db.sale.SaleDao
import java.math.BigDecimal


class Register @Throws(NoDaoSetException::class)
private constructor() {

    private var currentSale: Sale? = null
    var salesRecordTemplate:Sale?=null

    /**
     * Returns total price of Sale.
     * @return total price of Sale.
     */
    val total: BigDecimal
        get() = if (currentSale == null) BigDecimal.ZERO else currentSale!!.total

    val totalDiscount:BigDecimal
        get() = if (currentSale == null) BigDecimal.ZERO else currentSale!!.totalDiscount


    val totaltax:BigDecimal
        get() = if (currentSale == null) BigDecimal.ZERO else currentSale!!.totaltax



    init {
        if (!isDaoSet) {
            throw NoDaoSetException()
        }
        stock = Inventory.getInstance().getStock()

    }

    /**
     * Initiates a new Sale.
     * @param startTime time that sale created.
     * @return Sale that created.
     */
    fun initiateSale(sale: Sale): Sale {
        if (currentSale != null) {
            return currentSale as Sale
        }
        currentSale =  saleDao!!.initiateSale(sale!!)
        return currentSale as Sale
    }

    /**
     * Add Product to Sale.
     * @param product product to be added.
     * @param quantity quantity of product that added.
     * @return LineItem of Sale that just added.
     */
    fun addItem(product: Product?, quantity: BigDecimal): LineItem {

        if (product!!.is_tax_inclusive==true) {
            // tax excluded price  = item price / ((tax rate /100) + 1 )
            var taxExcludedPrice=product?.unitPrice/((product?.tax_percent.toBigDecimal().divide(100.toBigDecimal()))+BigDecimal.ONE)
            product?.unitPrice=taxExcludedPrice
            var itemTaxt=product?.unitPrice?.subtract(taxExcludedPrice)
            product?.tax_amount=itemTaxt
        }
        if (currentSale == null) {
                initiateSale(salesRecordTemplate!!)

        }

        val lineItem = currentSale!!.addLineItem(product!!, quantity)

        if (lineItem.id == LineItem.UNDEFINED) {
            val lineId = saleDao!!.addLineItem(currentSale!!.id, lineItem)
            lineItem.id = lineId
        } else {
            saleDao!!.updateLineItem(currentSale!!.id, lineItem)
        }

        return lineItem
    }

    fun getSalesLine(salesID:Int,lineID:Int):LineItem?
    {
        return saleDao!!.getLine(salesID,lineID)
    }

    /**
     * End the Sale.
     * @param endTime time that Sale ended.
     */
    fun endSale(endTime: String) {
        if (currentSale != null) {
            saleDao!!.endSale(currentSale!!, endTime)
            for (line in currentSale!!.allLineItem) {
                stock!!.updateStockSum(line.product!!.productId, line.quantity)
            }
            currentSale = null
        }
    }

    /**
     * Returns the current Sale of this Register.
     * @return the current Sale of this Register.
     */
    fun getCurrentSale(): Sale? {
        if (currentSale == null)
            initiateSale(salesRecordTemplate!!)
        return currentSale
    }

    /**
     * Sets the current Sale of this Register.
     * @param id of Sale to retrieve.
     * @return true if success to load Sale from ID; otherwise false.
     */
    fun setCurrentSale(id: Int): Boolean {
        currentSale = saleDao!!.getSaleById(id)
        return false
    }

    fun setCurrentSalesNull() {
        currentSale = null
    }


    /**
     * Determines that if there is a Sale handling or not.
     * @return true if there is a current Sale; otherwise false.
     */
    fun hasSale(): Boolean {
        return if (currentSale == null) false else true
    }

    /**
     * Cancels the current Sale.
     */
    fun cancleSale() {
        if (currentSale != null) {
            saleDao!!.cancelSale(currentSale!!, DateTimeStrategy.currentTime)
            currentSale = null
        }
    }

    /**
     * Edit the specific LineItem
     * @param saleId ID of LineItem to be edited.
     * @param lineItem LineItem to be edited.
     * @param quantity a new quantity to set.
     * @param priceAtSale a new priceAtSale to set.
     */
    fun updateItem(saleId: Int, lineItem: LineItem, quantity: BigDecimal, priceAtSale: BigDecimal) {
        lineItem.setUnitPriceAtSale(priceAtSale)
        lineItem.quantity = quantity
        saleDao!!.updateLineItem(saleId, lineItem)
    }

    /**
     * Removes LineItem from the current Sale.
     * @param lineItem lineItem to be removed.
     */
    fun removeItem(lineItem: LineItem) {
        saleDao!!.removeLineItem(lineItem.id)
        currentSale!!.removeItem(lineItem)
        if (currentSale!!.allLineItem.isEmpty()) {
            cancleSale()
        }
    }

    companion object {
        private var instance: Register? = null
        private var saleDao: SaleDao? = null
        private var stock: Stock? = null

        /**
         * Determines whether the DAO already set or not.
         * @return true if the DAO already set; otherwise false.
         */
        val isDaoSet: Boolean
            get() = saleDao != null

        @Throws(NoDaoSetException::class)
       internal fun getInstance(): Register {
            if (instance == null) instance = Register()
            return instance as Register
        }

        /**
         * Injects its sale DAO
         * @param dao DAO of sale
         */
       internal fun setSaleDao(dao: SaleDao) {
            saleDao = dao
        }
    }

}