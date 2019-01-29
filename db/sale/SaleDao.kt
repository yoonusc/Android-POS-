package com.ionob.pos.db.sale

import java.util.Calendar

import com.ionob.pos.domain.inventory.LineItem
import com.ionob.pos.domain.sale.Sale

/**
 * DAO for Sale process.
 *
 * @author Ionob Team
 */
interface SaleDao {

    /**
     * Returns all sale in the records.
     * @return all sale in the records.
     */

    val allSale: List<Sale>
    val allCompletedSale: List<Sale>
    val allDraftSale: List<Sale>
    val allSynced: List<Sale>
    val allFailed: List<Sale>
    fun getCoutOfRecord(status:String): Int

    /**
     * Initiates a new Sale.
     * @param startTime time that Sale initiated.
     * @return Sale that initiated
     */
    fun initiateSale(startTime: String): Sale?


    fun initiateSale(sales: Sale): Sale

    /**
     * End Sale
     * @param sale Sale to be ended.
     * @param endTime time that Sale ended.
     */
    fun endSale(sale: Sale, endTime: String)

    /**
     * Add LineItem to Sale.
     * @param saleId ID of the Sale to add LineItem.
     * @param lineItem LineItem to be added.
     * @return ID of LineItem that just added.
     */

    fun updateSale(sale: Sale)


    fun addLineItem(saleId: Int, lineItem: LineItem): Int

    /**
     * Returns the Sale with specific ID.
     * @param id ID of specific Sale.
     * @return the Sale with specific ID.
     */
    fun getSaleById(id: Int): Sale

    /**
     * Clear all records in SaleLedger.
     */
    fun clearSaleLedger()


    fun deleteSalesRecord(salesId:Int)

    /**
     * Returns list of LineItem that belong to Sale with specific Sale ID.
     * @param saleId ID of sale.
     * @return list of LineItem that belong to Sale with specific Sale ID.
     */
    fun getLineItem(saleId: Int): List<LineItem>


    fun getLine(saleId: Int,lineid:Int): LineItem?

    /**
     * Updates the data of specific LineItem.
     * @param saleId ID of Sale that this LineItem belong to.
     * @param lineItem to be updated.
     */
    fun updateLineItem(saleId: Int, lineItem: LineItem)

    /**
     * Returns list of Sale with scope of time.
     * @param start start bound of scope.
     * @param end end bound of scope.
     * @return list of Sale with scope of time.
     */
    fun getAllSaleDuring(start: Calendar, end: Calendar): List<Sale>

    /**
     * Cancel the Sale.
     * @param sale Sale to be cancel.
     * @param endTime time that cancelled.
     */
    fun cancelSale(sale: Sale, endTime: String)

    /**
     * Removes LineItem.
     * @param id of LineItem to be removed.
     */
    fun removeLineItem(id: Int)

    companion object {
        val STATUS_SYNCED="SYNCED"
        val STATUS_SYNC_FAILED="FAILED"
        val STATUS_COMPLETED="COMLETED"
        val STATUS_DRAFT="DRAFT"
        val STATUS_ALL="all"
    }
}
