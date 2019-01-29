package com.ionob.pos.domain.sale

import java.util.Calendar

import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.db.sale.SaleDao

/**
 * Book that keeps sale record.
 *
 * @author Ionob Team
 */
class SalesRcord @Throws(NoDaoSetException::class)
private constructor() {

    /**
     * Returns all sale in the records.
     * @return all sale in the records.
     */
    val allSale: List<Sale>
        get() = saleDao!!.allSale

    val allDraft: List<Sale>
        get() = saleDao!!.allDraftSale

    val allCompleted: List<Sale>
        get() = saleDao!!.allCompletedSale

    val allSynced: List<Sale>
        get() = saleDao!!.allSynced

    val allFailed: List<Sale>
        get() = saleDao!!.allFailed

    fun getCount(status:String):Int {
        return  saleDao!!.getCoutOfRecord(status)
    }


    init {
        if (!isDaoSet) {
            throw NoDaoSetException()
        }
    }

    /**
     * Returns the Sale with specific ID.
     * @param id ID of specific Sale.
     * @return the Sale with specific ID.
     */
    fun getSaleRcordById(id: Int): Sale {
        return saleDao!!.getSaleById(id)
    }

    /**
     * Clear all records in SaleLedger.
     */
    fun clearSaleLedger() {
        saleDao!!.clearSaleLedger()
    }

    fun deleteSalesRecord(salesId:Int) {
        saleDao!!.deleteSalesRecord(salesId)
    }


    /**
     * Returns list of Sale with scope of time.
     * @param start start bound of scope.
     * @param end end bound of scope.
     * @return list of Sale with scope of time.
     */
    fun getAllSaleDuring(start: Calendar, end: Calendar): List<Sale> {
        return saleDao!!.getAllSaleDuring(start, end)
    }

    companion object {

        private var instance: SalesRcord? = null
        private var saleDao: SaleDao? = null

        /**
         * Determines whether the DAO already set or not.
         * @return true if the DAO already set; otherwise false.
         */
        val isDaoSet: Boolean
            get() = saleDao != null

        @Throws(NoDaoSetException::class)
        internal fun getInstance(): SalesRcord {
            if (instance == null) instance = SalesRcord()
            return instance as SalesRcord
        }

        /**
         * Sets the database connector.
         * @param dao Data Access Object of Sale.
         */
        internal fun setSaleDao(dao: SaleDao) {
            saleDao = dao
        }
    }
}
