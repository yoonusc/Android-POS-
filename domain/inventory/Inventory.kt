package com.ionob.pos.domain.inventory

import com.ionob.pos.db.NoDaoSetException
import com.ionob.pos.db.inventory.InventoryDao


/**
 * This class is service locater for Product Catalog and Stock.
 *
 * @author Ionob Team
 */
class Inventory
/**
 * Constructs Data Access Object of inventory.
 * @throws NoDaoSetException if DAO is not exist.
 */
@Throws(NoDaoSetException::class) constructor() {

    /**
     * Returns stock using in this inventory.
     * @return stock using in this inventory.
     */
    val stock: Stock
    /**
     * Returns product catalog using in this inventory.
     * @return product catalog using in this inventory.
     */
     private val productCatalog: ProductCatalog

    init {
        if (!isDaoSet()) {
            throw NoDaoSetException()
        }
        stock = Stock(inventoryDao)
        productCatalog = ProductCatalog(inventoryDao)
    }

    internal fun getStock(): Stock {
        return stock
    }

     public fun getProductCatalog(): ProductCatalog {
        return productCatalog
    }


    companion object {
        private var instance: Inventory? = null
        private var inventoryDao: InventoryDao? = null


        /**
         * Determines whether the DAO already set or not.
         * @return true if the DAO already set; otherwise false.
         */

            fun isDaoSet():Boolean {
            if(inventoryDao != null)
            {
                return true
            }
            return false
            }

        /**
         * Sets the database connector.
         * @param dao Data Access Object of inventory.
         */
       internal fun setInventoryDao(dao: InventoryDao) {
          this.inventoryDao = dao
        }

        /**
         * Returns the instance of this singleton class.
         * @return instance of this class.
         * @throws NoDaoSetException if DAO was not set.
         */
        @Throws(NoDaoSetException::class)
       internal fun getInstance(): Inventory {
            if (instance == null)
                instance = Inventory()
            return instance as Inventory
        }
    }


}

