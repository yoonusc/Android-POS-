package com.ionob.pos.domain.inventory

import com.ionob.pos.db.inventory.InventoryDao
import java.math.BigDecimal

/**
 * Import log of ProductLot come in to store.
 *
 * @author Ionob Team
 */
class Stock
/**
 * Constructs Data Access Object of inventory in ProductCatalog.
 * @param inventoryDao DAO of inventory.
 */
(private val inventoryDao: InventoryDao?) {

    /**
     * Returns all ProductLots in inventory.
     * @return all ProductLots in inventory.
     */
    val allProductLot: List<ProductLot>?
        get() = inventoryDao?.allProductLot

    /**
     * Constructs ProductLot and adds ProductLot to inventory.
     * @param dateAdded date added of ProductLot.
     * @param quantity quantity of ProductLot.
     * @param product product of ProductLot.
     * @param cost cost of ProductLot.
     * @return
     */
    fun addProductLot(dateAdded: String, quantity: Int, product: Product?, cost: Double): Boolean {
        val productLot = ProductLot(ProductLot.UNDEFINED_ID, dateAdded, quantity, product, cost)
        val id = inventoryDao?.addProductLot(productLot)
        return id != -1
    }

    /**
     * Returns list of ProductLot in inventory finds by id.
     * @param id id of ProductLot.
     * @return list of ProductLot in inventory finds by id.
     */
    fun getProductLotByProductId(id: Int): List<ProductLot>? {
        return inventoryDao?.getProductLotByProductId(id)
    }

    /**
     * Returns Stock in inventory finds by id.
     * @param id id of Stock.
     * @return Stock in inventory finds by id.
     */
    fun getStockSumById(id: Int): Int? {
        return inventoryDao?.getStockSumById(id)
    }

    /**
     * Updates quantity of product.
     * @param productId id of product.
     * @param quantity quantity of product.
     */
    fun updateStockSum(productId: Int?, quantity: BigDecimal) {
        inventoryDao?.updateStockSum(productId, quantity.toDouble())

    }

    /**
     * Clear Stock.
     */
    fun clearStock() {
        inventoryDao?.clearStock()

    }


}
